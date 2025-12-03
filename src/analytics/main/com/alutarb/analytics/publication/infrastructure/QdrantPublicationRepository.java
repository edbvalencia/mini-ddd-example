package com.alutarb.analytics.publication.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.alutarb.analytics.publication.domain.Publication;
import com.alutarb.analytics.publication.domain.PublicationRepository;
import com.alutarb.analytics.shared.infrastructure.VectorStoreUtils;
import com.alutarb.shared.domain.Constants;
import com.alutarb.shared.domain.PublicationSentimentType;

@Repository
public class QdrantPublicationRepository implements PublicationRepository {

    private final VectorStore vectorStore;

    public QdrantPublicationRepository(@Qualifier(Constants.CURRENT_EMBEDDING_COLLECTION) VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void save(Publication publication) {
        try {
            vectorStore.add(List.of(toDocument(publication)));
        } catch (Exception e) {
            throw new RuntimeException("error saving publication to vector store", e);
        }
    }

    @Override
    public void save(List<Publication> publications) {
        try {
            var documents = publications.stream().map(this::toDocument).toList();
            vectorStore.add(documents);
        } catch (Exception e) {
            throw new RuntimeException("error saving publications to vector store", e);
        }
    }

    @Override
    public List<Publication> searchByQuery(String query, int limit) {
        var request = SearchRequest.builder()
            .query(query)
            .topK(limit)
            .build();
        return toPublications(vectorStore.similaritySearch(request));
    }

    @Override
    public void deleteAll() {
        vectorStore.delete("true");
    }

    public List<Publication> toPublications(List<Document> documents) {
        return documents.stream()
            .map(this::toPublication)
            .toList();
    }

    public Publication toPublication(Document document) {
        var metadata = document.getMetadata();

        String id = (String) metadata.get("id");
        String text = (String) metadata.get("text");

        Integer likes = toInt(metadata.get("likes"));
        Integer shares = toInt(metadata.get("shares"));
        Integer views = toInt(metadata.get("views"));
        Integer comments = toInt(metadata.get("comments"));

        PublicationSentimentType sentiment = PublicationSentimentType.valueOf((String) metadata.get("sentiment"));

        List<String> hashtags = toStringList(metadata.get("hashtags"));
        List<String> mentions = toStringList(metadata.get("mentions"));
        List<String> emojis = toStringList(metadata.get("emojis"));
        List<String> concepts = toStringList(metadata.get("concepts"));
        List<String> verbs = toStringList(metadata.get("verbs"));

        Instant createdAt = Instant.parse((String) metadata.get("createdAt"));

        return new Publication(
            id,
            text,
            hashtags,
            mentions,
            emojis,
            concepts,
            verbs,
            likes,
            shares,
            views,
            comments,
            sentiment,
            createdAt
        );
    }

    private Document toDocument(Publication publication) {
        return new Document(
            VectorStoreUtils.toUuid(publication.id()),
            publication.text(),
            toMetadataEmbedding(publication)
        );
    }

    private Map<String, Object> toMetadataEmbedding(Publication publication) {
        return Map.ofEntries(
            Map.entry("id", publication.id()),
            Map.entry("text", publication.text()),
            Map.entry("likes", publication.likes()),
            Map.entry("shares", publication.shares()),
            Map.entry("views", publication.views()),
            Map.entry("comments", publication.comments()),
            Map.entry("sentiment", publication.sentiment().name()),
            Map.entry("hashtags", publication.hashtags()),
            Map.entry("mentions", publication.mentions()),
            Map.entry("emojis", publication.emojis()),
            Map.entry("concepts", publication.concepts()),
            Map.entry("verbs", publication.verbs()),
            Map.entry("createdAt", publication.createdAt().toString())
        );
    }

    private Integer toInt(Object value) {
        if (value == null) return null;
        if (value instanceof Integer i) return i;
        if (value instanceof Long l) return l.intValue();
        if (value instanceof Double d) return d.intValue();
        return Integer.valueOf(value.toString());
    }

    private List<String> toStringList(Object value) {
        if (value == null) return List.of();
        return ((List<?>) value).stream().map(Object::toString).toList();
    }
}
