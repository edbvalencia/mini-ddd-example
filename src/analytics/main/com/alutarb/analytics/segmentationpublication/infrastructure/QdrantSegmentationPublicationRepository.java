package com.alutarb.analytics.segmentationpublication.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.shared.infrastructure.VectorStoreUtils;

@Component
public class QdrantSegmentationPublicationRepository {

    public record SearchResult(String id, List<Double> embedding) {
    }

    private static final int MAX_TEXT_CHARS = 2000;
    private final VectorStore vectorStore;

    public QdrantSegmentationPublicationRepository(@Qualifier("segmentationpublication") VectorStore vectorStore) {
        this.vectorStore = vectorStore;
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

    public Map<String, List<Double>> searchWithEmbeddings(String query, int limit, Instant createdAtFrom,
        Instant createdAtTo) {
        var builder = SearchRequest.builder()
            .query(query)
            .topK(limit);

        String filterExpression = buildCreatedAtFilterExpression(createdAtFrom, createdAtTo);
        if (filterExpression != null) {
            builder.filterExpression(filterExpression);
        }

        var request = builder.build();
        var documents = vectorStore.similaritySearch(request);

        Map<String, List<Double>> embeddingsById = new java.util.LinkedHashMap<>();
        for (var doc : documents) {
            String id = (String) doc.getMetadata().get("id");
            if (id != null) {
                try {
                    Object embeddingObj = doc.getMetadata().get("embedding");
                    if (embeddingObj instanceof List<?> embList) {
                        List<Double> embedding = new ArrayList<>();
                        for (Object obj : embList) {
                            if (obj instanceof Number num) {
                                embedding.add(num.doubleValue());
                            }
                        }
                        if (!embedding.isEmpty()) {
                            embeddingsById.put(id, embedding);
                        }
                    }
                } catch (Exception e) {
                }
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
        put(metadata, "createdAtEpochMs", toEpochMillis(publication.createdAt()));
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

    private String buildCreatedAtFilterExpression(Instant createdAtFrom, Instant createdAtTo) {
        Long fromMs = toEpochMillis(createdAtFrom);
        Long toMs = toEpochMillis(createdAtTo);

        if (fromMs == null && toMs == null) {
            return null;
        }

        if (fromMs != null && toMs != null) {
            return "createdAtEpochMs >= " + fromMs + " && createdAtEpochMs <= " + toMs;
        }

        if (fromMs != null) {
            return "createdAtEpochMs >= " + fromMs;
        }

        return "createdAtEpochMs <= " + toMs;
    }

    private String safeText(String text) {
        if (text == null) return "";
        if (text.length() <= MAX_TEXT_CHARS) return text;
        return text.substring(0, MAX_TEXT_CHARS);
    }

}
