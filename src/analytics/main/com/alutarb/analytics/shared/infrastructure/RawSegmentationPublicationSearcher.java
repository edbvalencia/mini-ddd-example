package com.alutarb.analytics.shared.infrastructure;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alutarb.analytics.shared.domain.RawMention;

@Service
public class RawSegmentationPublicationSearcher {

    private static final Instant FIXED_FROM = LocalDate.of(2026, 1, 12).atStartOfDay(ZoneOffset.UTC).toInstant();
    private static final Instant FIXED_TO = LocalDate.of(2026, 1, 24).atStartOfDay(ZoneOffset.UTC).toInstant();
    private static final int PAGE_SIZE = 1000;

    private final MongoTemplate mongo;

    public RawSegmentationPublicationSearcher(@Qualifier("rawMongoTemplate") MongoTemplate mongo) {
        this.mongo = mongo;
    }

    public List<RawMention> search(int offset, int size) {
        Query query = new Query()
            .skip(offset)
            .limit(size)
            .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongo.find(query, RawMention.class, "segmentation");
    }

    public List<RawMention> searchFixedDateRange(int offset, int size) {
        return searchByDateRange(FIXED_FROM, FIXED_TO, offset, size);
    }

    public List<RawMention> searchAllFixedDateRange() {
        List<RawMention> allResults = new ArrayList<>();
        int offset = 0;
        List<RawMention> page;

        do {
            page = searchFixedDateRange(offset, PAGE_SIZE);
            allResults.addAll(page);
            offset += PAGE_SIZE;
        } while (page.size() == PAGE_SIZE);

        return allResults;
    }

    public List<RawMention> searchByDateRange(Instant from, Instant to, int offset, int size) {
        Query query = new Query()
            .addCriteria(Criteria.where("createdAt").gte(from).lt(to))
            .skip(offset)
            .limit(size)
            .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongo.find(query, RawMention.class, "segmentation");
    }

    public long countFixedDateRange() {
        Query query = new Query()
            .addCriteria(Criteria.where("createdAt").gte(FIXED_FROM).lt(FIXED_TO));
        return mongo.count(query, RawMention.class, "segmentation");
    }

}