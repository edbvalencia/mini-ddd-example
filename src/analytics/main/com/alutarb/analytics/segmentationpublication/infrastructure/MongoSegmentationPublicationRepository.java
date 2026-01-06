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

@Repository
public class MongoSegmentationPublicationRepository implements SegmentationPublicationRepository {

    private final MongoTemplate template;
    private final QdrantSegmentationPublicationRepository qdrantRepository;

    public MongoSegmentationPublicationRepository(
        @Qualifier("segmentationMongoTemplate") MongoTemplate template,
        QdrantSegmentationPublicationRepository qdrantRepository
    ) {
        this.template = template;
        this.qdrantRepository = qdrantRepository;
    }

    @Override
    public List<SegmentationPublication> searchByQuery(String query, int limit) {
        return searchByQuery(query, limit, null, null);
    }

    @Override
    public List<SegmentationPublication> searchByQuery(String query, int limit, Instant createdAtFrom,
        Instant createdAtTo) {
        var ids = qdrantRepository.searchIdsByQuery(query, limit, createdAtFrom, createdAtTo);

        if (ids.isEmpty()) {
            return List.of();
        }

        var mongoQuery = new Query(
            Criteria.where("_id").in(ids)
        );

        var publications = template.find(mongoQuery, SegmentationPublication.class);

        Map<String, SegmentationPublication> byId = publications.stream()
            .collect(Collectors.toMap(SegmentationPublication::id, Function.identity()));

        return ids.stream()
            .map(byId::get)
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
        var ids = qdrantRepository.searchIdsByQuery(query, limit, createdAtFrom, createdAtTo);

        if (ids.isEmpty()) {
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

        var mongoQuery = new Query(Criteria.where("_id").in(ids));
        var publications = template.find(mongoQuery, SegmentationPublication.class);

        Map<String, SegmentationPublication> byId = publications.stream()
            .collect(Collectors.toMap(SegmentationPublication::id, Function.identity()));

        List<SegmentationPublication> ordered = ids.stream()
            .map(byId::get)
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