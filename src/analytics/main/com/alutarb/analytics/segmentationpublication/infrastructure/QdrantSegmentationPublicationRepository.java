package com.alutarb.analytics.segmentationpublication.infrastructure;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublicationRepository;
import com.alutarb.analytics.shared.infrastructure.VectorStoreUtils;

@Repository
public class QdrantSegmentationPublicationRepository implements SegmentationPublicationRepository {

    private static final int MAX_TEXT_CHARS = 2000;
    private final VectorStore vectorStore;

    public QdrantSegmentationPublicationRepository(@Qualifier("segmentationpublication") VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void save(SegmentationPublication publication) {
        try {
            vectorStore.add(List.of(toDocument(publication)));
        } catch (Exception e) {
            throw new RuntimeException("error saving segmentation publication to vector store", e);
        }
    }

    @Override
    public void save(List<SegmentationPublication> publications) {
        try {
            var documents = publications.stream().map(this::toDocument).toList();
            vectorStore.add(documents);
        } catch (Exception e) {
            throw new RuntimeException("error saving segmentation publications to vector store", e);
        }
    }

    @Override
    public List<SegmentationPublication> searchByQuery(String query, int limit) {
        var request = SearchRequest.builder()
            .query(query)
            .topK(limit)
            .build();
        return vectorStore.similaritySearch(request).stream()
            .map(this::toPublication)
            .toList();
    }

    @Override
    public void deleteAll() {
        vectorStore.delete("true");
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
        put(metadata, "audience", publication.audience());
        put(metadata, "avatar", publication.avatar());
        put(metadata, "comments", publication.comments());
        put(metadata, "createdAt", toString(publication.createdAt()));
        put(metadata, "dataType", publication.dataType());
        put(metadata, "impactLevel", publication.impactLevel());
        put(metadata, "interactions", publication.interactions());
        put(metadata, "itemType", publication.itemType());
        put(metadata, "link", publication.link());
        put(metadata, "media", publication.media());
        put(metadata, "network", publication.network());
        put(metadata, "page", publication.page());
        put(metadata, "platform", publication.platform());
        put(metadata, "reachLevel", publication.reachLevel());
        put(metadata, "reactions", publication.reactions());
        put(metadata, "registeredAt", toString(publication.registeredAt()));
        put(metadata, "shares", publication.shares());
        put(metadata, "text", safeText(publication.text()));
        put(metadata, "bigFive", publication.bigFive());
        put(metadata, "cleanText", publication.cleanText());
        put(metadata, "color", publication.color());
        put(metadata, "emotion", publication.emotion());
        put(metadata, "gobColor", publication.gobColor());
        put(metadata, "isValid", publication.isValid());
        put(metadata, "municipality", publication.municipality());
        put(metadata, "subtopic", publication.subtopic());
        put(metadata, "summary", publication.summary());
        put(metadata, "title", publication.title());
        put(metadata, "topic", publication.topic());
        put(metadata, "validText", publication.validText());

        return metadata;
    }

    private SegmentationPublication toPublication(Document document) {
        var metadata = document.getMetadata();

        return new SegmentationPublication(
            (String) metadata.get("id"),
            toLong(metadata.get("audience")),
            (String) metadata.get("avatar"),
            toLong(metadata.get("comments")),
            parseInstant(metadata.get("createdAt")),
            (String) metadata.get("dataType"),
            (String) metadata.get("impactLevel"),
            toLong(metadata.get("interactions")),
            (String) metadata.get("itemType"),
            (String) metadata.get("link"),
            (String) metadata.get("media"),
            (String) metadata.get("network"),
            (String) metadata.get("page"),
            (String) metadata.get("platform"),
            (String) metadata.get("reachLevel"),
            toLong(metadata.get("reactions")),
            parseInstant(metadata.get("registeredAt")),
            toLong(metadata.get("shares")),
            (String) metadata.get("text"),
            (Map<String, Object>) metadata.get("bigFive"),
            (String) metadata.get("cleanText"),
            (String) metadata.get("color"),
            (String) metadata.get("emotion"),
            (String) metadata.get("gobColor"),
            (Boolean) metadata.get("isValid"),
            (String) metadata.get("municipality"),
            (String) metadata.get("subtopic"),
            (String) metadata.get("summary"),
            (String) metadata.get("title"),
            (String) metadata.get("topic"),
            (Boolean) metadata.get("validText")
        );
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
