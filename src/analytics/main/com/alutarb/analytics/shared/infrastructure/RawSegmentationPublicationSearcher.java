package com.alutarb.analytics.shared.infrastructure;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alutarb.analytics.shared.domain.RawMention;

@Service
public class RawSegmentationPublicationSearcher {

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

}