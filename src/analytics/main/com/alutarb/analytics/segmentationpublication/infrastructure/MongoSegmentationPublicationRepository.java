package com.alutarb.analytics.segmentationpublication.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublicationQueryStats;
import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublicationRepository;
import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublicationSearchResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MongoSegmentationPublicationRepository implements SegmentationPublicationRepository {

    @Qualifier("segmentationMongoTemplate")
    private final MongoTemplate template;
    private final QdrantSegmentationPublicationRepository qdrantRepository;

    @Override
    public List<SegmentationPublication> searchByQuery(String query, int limit) {
        return searchByQuery(query, limit, null, null);
    }

    @Override
    public List<SegmentationPublication> searchByQuery(
        String query,
        int limit,
        Instant createdAtFrom,
        Instant createdAtTo
    ) {
        var embeddingsById = qdrantRepository.searchWithEmbeddings(query, limit, createdAtFrom, createdAtTo, null,
            null);

        if (embeddingsById.isEmpty()) {
            return List.of();
        }

        var ids = List.copyOf(embeddingsById.keySet());
        var mongoQuery = new Query(Criteria.where("_id").in(ids));
        var publications = template.find(mongoQuery, SegmentationPublication.class);

        Map<String, SegmentationPublication> byId = publications.stream()
            .collect(Collectors.toMap(SegmentationPublication::id, Function.identity()));

        return ids.stream()
            .map(id -> {
                var pub = byId.get(id);
                if (pub == null) return null;
                var embedding = embeddingsById.get(id);
                return new SegmentationPublication(
                    pub.id(),
                    pub.audience(),
                    pub.comments(),
                    pub.interactions(),
                    pub.reactions(),
                    pub.shares(),
                    pub.socialNetwork(),
                    pub.text(),
                    pub.createdAt(),
                    embedding,
                    pub.color(),
                    pub.emotion()
                );
            })
            .filter(p -> p != null)
            .toList();
    }

    @Override
    public SegmentationPublicationSearchResult searchByQueryWithStats(String query, int limit) {
        return searchByQueryWithStats(query, limit, null, null);
    }

    @Override
    public SegmentationPublicationSearchResult searchByQueryWithStats(String query, int limit, Instant createdAtFrom,
        Instant createdAtTo) {
        return searchByQueryWithStats(query, limit, createdAtFrom, createdAtTo, null, null);
    }

    @Override
    public SegmentationPublicationSearchResult searchByQueryWithStats(
        String query,
        int limit,
        Instant createdAtFrom,
        Instant createdAtTo,
        Double scoreThreshold,
        String payloadFilterExpression
    ) {
        var embeddingsById = qdrantRepository.searchWithEmbeddings(
            query,
            limit,
            createdAtFrom,
            createdAtTo,
            scoreThreshold,
            payloadFilterExpression
        );

        if (embeddingsById.isEmpty()) {
            return new SegmentationPublicationSearchResult(List.of(), SegmentationPublicationQueryStats.empty());
        }

        var ids = List.copyOf(embeddingsById.keySet());

        var mongoQuery = new Query(Criteria.where("_id").in(ids));
        var publications = template.find(mongoQuery, SegmentationPublication.class);

        if (publications.isEmpty()) {
            return new SegmentationPublicationSearchResult(List.of(), SegmentationPublicationQueryStats.empty());
        }

        var matchStage = Aggregation.match(Criteria.where("_id").in(ids));

        var aggregation = Aggregation.newAggregation(
            matchStage,
            Aggregation.facet(
                Aggregation.limit(ids.size())
            ).as("publications").and(
                Aggregation.group()
                    .sum("audience").as("audience")
                    .sum("comments").as("comments")
                    .sum("interactions").as("interactions")
                    .sum("reactions").as("reactions")
                    .sum("shares").as("shares")
                    .count().as("publicationsCount")
            ).as("stats")
        );

        AggregationResults<Document> raw = template.aggregate(
            aggregation,
            template.getCollectionName(SegmentationPublication.class),
            Document.class
        );
        Document doc = raw.getUniqueMappedResult();
        if (doc == null) {
            return new SegmentationPublicationSearchResult(List.of(), SegmentationPublicationQueryStats.empty());
        }

        @SuppressWarnings("unchecked") List<Document> statsDocs = (List<Document>) doc.get("stats");

        Map<String, SegmentationPublication> byId = publications.stream()
            .collect(Collectors.toMap(SegmentationPublication::id, Function.identity()));

        List<SegmentationPublication> ordered = ids.stream()
            .map(id -> {
                var pub = byId.get(id);
                if (pub == null) return null;
                var embedding = embeddingsById.get(id);
                return new SegmentationPublication(
                    pub.id(),
                    pub.audience(),
                    pub.comments(),
                    pub.interactions(),
                    pub.reactions(),
                    pub.shares(),
                    pub.socialNetwork(),
                    pub.text(),
                    pub.createdAt(),
                    embedding,
                    pub.color(),
                    pub.emotion()
                );
            })
            .filter(p -> p != null)
            .toList();

        SegmentationPublicationQueryStats stats = SegmentationPublicationQueryStats.empty();
        if (statsDocs != null && !statsDocs.isEmpty()) {
            Document s = statsDocs.get(0);
            stats = new SegmentationPublicationQueryStats(
                getLong(s, "audience"),
                getLong(s, "comments"),
                getLong(s, "interactions"),
                getLong(s, "reactions"),
                getLong(s, "shares"),
                getInt(s, "publicationsCount")
            );
        }

        return new SegmentationPublicationSearchResult(ordered, stats);
    }

    private Long getLong(Document doc, String key) {
        Object value = doc.get(key);
        if (value == null) return 0L;
        if (value instanceof Number n) return n.longValue();
        return Long.valueOf(value.toString());
    }

    private int getInt(Document doc, String key) {
        Object value = doc.get(key);
        if (value == null) return 0;
        if (value instanceof Number n) return n.intValue();
        return Integer.parseInt(value.toString());
    }

    @Override
    public void save(SegmentationPublication publication) {
        template.save(publication);
        qdrantRepository.save(publication);
    }

}