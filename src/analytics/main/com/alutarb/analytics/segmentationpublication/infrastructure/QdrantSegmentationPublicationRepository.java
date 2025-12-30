package com.alutarb.analytics.segmentationpublication.infrastructure;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.shared.infrastructure.VectorStoreUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QdrantSegmentationPublicationRepository {

    private static final int MAX_TEXT_CHARS = 2000;
    private final VectorStore vectorStore;

    public void save(SegmentationPublication publication) {
        try {
            vectorStore.add(List.of(toDocument(publication)));
        } catch (Exception e) {
            throw new RuntimeException("error saving segmentation publication to vector store", e);
        }
    }

    public List<String> searchIdsByQuery(String query, int limit) {
        var request = SearchRequest.builder()
            .query(query)
            .topK(limit)
            .build();
        return vectorStore.similaritySearch(request).stream()
            .map(document -> (String) document.getMetadata().get("id"))
            .toList();
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

    private String safeText(String text) {
        if (text == null) return "";
        if (text.length() <= MAX_TEXT_CHARS) return text;
        return text.substring(0, MAX_TEXT_CHARS);
    }
}
