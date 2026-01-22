package com.alutarb.analytics.shared.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alutarb.analytics.shared.domain.RawMention;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.PointId;
import io.qdrant.client.grpc.Points.RetrievedPoint;
import io.qdrant.client.grpc.Points.WithPayloadSelector;
import io.qdrant.client.grpc.Points.WithVectorsSelector;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RawMentionQdrantRepository {

    private static final String COLLECTION_NAME = "rawmention";
    private static final int MAX_TEXT_CHARS = 2000;

    private final VectorStore vectorStore;
    private final QdrantClient qdrantClient;
    private final EmbeddingModel embeddingModel;

    public RawMentionQdrantRepository(@Qualifier("rawmention") VectorStore vectorStore, QdrantClient qdrantClient,
        EmbeddingModel embeddingModel) {
        this.vectorStore = vectorStore;
        this.qdrantClient = qdrantClient;
        this.embeddingModel = embeddingModel;
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

    public void save(RawMention rawMention) {
        var document = toDocument(rawMention);
        vectorStore.add(List.of(document));
    }

    private Document toDocument(RawMention rawMention) {
        return new Document(
            VectorStoreUtils.toUuid(rawMention.id()),
            rawMention.text() != null ? safeText(rawMention.text()) : "",
            toMetadataEmbedding(rawMention)
        );
    }

    private Map<String, Object> toMetadataEmbedding(RawMention rawMention) {
        Map<String, Object> metadata = new HashMap<>();
        put(metadata, "id", rawMention.id());
        put(metadata, "createdAt", toString(rawMention.createdAt()));
        put(metadata, "createdAtSec", toEpochSeconds(rawMention.createdAt()));
        put(metadata, "color", rawMention.color());
        put(metadata, "emotion", rawMention.emotion());
        put(metadata, "network", rawMention.network());
        put(metadata, "isValid", rawMention.isValid());
        return metadata;
    }

    private void put(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private String toString(Instant value) {
        return value != null ? value.toString() : null;
    }

    private Integer toEpochSeconds(Instant value) {
        return value != null ? (int) value.getEpochSecond() : null;
    }

    private String safeText(String text) {
        if (text == null) return "";
        return text.length() > MAX_TEXT_CHARS ? text.substring(0, MAX_TEXT_CHARS) : text;
    }

    private String buildFilterExpression(Instant createdAtFrom, Instant createdAtTo, String payloadFilterExpression) {
        String dateFilter = buildCreatedAtFilterExpression(createdAtFrom, createdAtTo);

        if (payloadFilterExpression == null || payloadFilterExpression.isBlank()) {
            return dateFilter;
        }

        if (dateFilter == null || dateFilter.isBlank()) {
            return payloadFilterExpression;
        }

        return dateFilter + " && " + payloadFilterExpression;
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
}
