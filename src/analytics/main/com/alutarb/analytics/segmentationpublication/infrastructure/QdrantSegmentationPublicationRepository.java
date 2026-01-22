package com.alutarb.analytics.segmentationpublication.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.shared.infrastructure.VectorStoreUtils;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.PointId;
import io.qdrant.client.grpc.Points.RetrievedPoint;
import io.qdrant.client.grpc.Points.WithPayloadSelector;
import io.qdrant.client.grpc.Points.WithVectorsSelector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QdrantSegmentationPublicationRepository {

    public record SearchResult(String id, List<Double> embedding) {
    }

    private static final int MAX_TEXT_CHARS = 2000;
    private static final String COLLECTION_NAME = "segmentationpublication";
    private final VectorStore vectorStore;
    private final QdrantClient qdrantClient;

    public QdrantSegmentationPublicationRepository(
        @Qualifier("segmentationpublication") VectorStore vectorStore,
        QdrantClient qdrantClient
    ) {
        this.vectorStore = vectorStore;
        this.qdrantClient = qdrantClient;
    }

    public void save(SegmentationPublication publication) {
        try {
            vectorStore.add(List.of(toDocument(publication)));
        } catch (Exception e) {
            throw new RuntimeException("error saving segmentation publication to vector store", e);
        }
    }

    public List<String> searchIdsByQuery(String query, int limit) {
        return searchIdsByQuery(query, limit, null, null);
    }

    public List<String> searchIdsByQuery(String query, int limit, Instant createdAtFrom, Instant createdAtTo) {
        var builder = SearchRequest.builder()
            .query(query)
            .topK(limit);

        String filterExpression = buildCreatedAtFilterExpression(createdAtFrom, createdAtTo);
        if (filterExpression != null) {
            builder.filterExpression(filterExpression);
        }

        var request = builder.build();

        return vectorStore.similaritySearch(request).stream()
            .map(document -> (String) document.getMetadata().get("id"))
            .toList();
    }

    public Map<String, List<Double>> searchWithEmbeddings(
        String query,
        int limit,
        Instant createdAtFrom,
        Instant createdAtTo,
        Double scoreThreshold,
        String payloadFilterExpression
    ) {
        var builder = SearchRequest.builder()
            .query(query)
            .topK(limit);

        String filterExpression = buildFilterExpression(createdAtFrom, createdAtTo, payloadFilterExpression);
        if (filterExpression != null) {
            builder.filterExpression(filterExpression);
        }

        if (scoreThreshold != null) {
            builder.similarityThreshold(scoreThreshold);
        }

        var request = builder.build();
        var documents = vectorStore.similaritySearch(request);

        Map<String, List<Double>> embeddingsById = new java.util.LinkedHashMap<>();
        List<PointId> pointIds = new ArrayList<>();
        Map<String, String> uuidToId = new HashMap<>();

        for (var doc : documents) {
            String id = (String) doc.getMetadata().get("id");
            if (id != null) {
                String uuid = VectorStoreUtils.toUuid(id);
                pointIds.add(PointId.newBuilder().setUuid(uuid).build());
                uuidToId.put(uuid, id);
            }
        }

        if (!pointIds.isEmpty()) {
            try {
                var withVectors = WithVectorsSelector.newBuilder().setEnable(true).build();
                var withPayload = WithPayloadSelector.newBuilder().setEnable(false).build();

                List<RetrievedPoint> response = qdrantClient.retrieveAsync(
                    COLLECTION_NAME,
                    pointIds,
                    withPayload,
                    withVectors,
                    null
                ).get();

                for (RetrievedPoint point : response) {
                    String uuid = point.getId().getUuid();
                    String id = uuidToId.get(uuid);

                    if (id != null && point.hasVectors()) {
                        var vectors = point.getVectors();
                        if (vectors.hasVector()) {
                            var vectorData = vectors.getVector().getDataList();
                            List<Double> embedding = new ArrayList<>(vectorData.size());
                            for (float f : vectorData) {
                                embedding.add((double) f);
                            }
                            embeddingsById.put(id, embedding);
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error retrieving embeddings from Qdrant", e);
                Thread.currentThread().interrupt();
            }
        }

        return embeddingsById;
    }

    private Document toDocument(SegmentationPublication publication) {
        return new Document(
            VectorStoreUtils.toUuid(publication.id()),
            publication.text() != null ? safeText(publication.text()) : "",
            toMetadataEmbedding(publication)
        );
    }

    private Map<String, Object> toMetadataEmbedding(SegmentationPublication publication) {
        Map<String, Object> metadata = new HashMap<>();
        put(metadata, "id", publication.id());
        put(metadata, "createdAt", toString(publication.createdAt()));
        put(metadata, "createdAtSec", toEpochSeconds(publication.createdAt()));
        put(metadata, "color", publication.color());
        put(metadata, "emotion", publication.emotion());
        put(metadata, "gobColor", publication.gobColor());
        put(metadata, "registeredAt", toString(publication.registeredAt()));
        return metadata;
    }

    private void put(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long l) return l;
        if (value instanceof Integer i) return i.longValue();
        if (value instanceof Double d) return d.longValue();
        return Long.valueOf(value.toString());
    }

    private Instant parseInstant(Object value) {
        if (value == null) return null;
        if (value instanceof Instant i) return i;
        return Instant.parse(value.toString());
    }

    private String toString(Instant value) {
        return value != null ? value.toString() : null;
    }

    private Long toEpochMillis(Instant value) {
        return value != null ? value.toEpochMilli() : null;
    }

    private Integer toEpochSeconds(Instant value) {
        return value != null ? (int) value.getEpochSecond() : null;
    }

    private String buildCreatedAtFilterExpression(Instant createdAtFrom, Instant createdAtTo) {
        Integer fromSec = toEpochSeconds(createdAtFrom);
        Integer toSec = toEpochSeconds(createdAtTo);

        if (fromSec == null && toSec == null) {
            return null;
        }

        if (fromSec != null && toSec != null) {
            return "createdAtSec >= " + fromSec + " && createdAtSec <= " + toSec;
        }

        if (fromSec != null) {
            return "createdAtSec >= " + fromSec;
        }

        return "createdAtSec <= " + toSec;
    }

    private String buildFilterExpression(Instant createdAtFrom, Instant createdAtTo, String payloadFilterExpression) {
        String dateFilter = buildCreatedAtFilterExpression(createdAtFrom, createdAtTo);

        if (payloadFilterExpression == null || payloadFilterExpression.isBlank()) {
            return dateFilter;
        }

        if (dateFilter == null || dateFilter.isBlank()) {
            return payloadFilterExpression;
        }

        return dateFilter + " && (" + payloadFilterExpression + ")";
    }

    private String safeText(String text) {
        if (text == null) return "";
        if (text.length() <= MAX_TEXT_CHARS) return text;
        return text.substring(0, MAX_TEXT_CHARS);
    }

}
