package com.alutarb.analytics.shared.infrastructure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class SegmentationMongoConfig {

    @Bean(name = "segmentationMongoClient")
    public MongoClient segmentationMongoClient(
        @Value("${spring.data.mongodb.segmentation.uri}") String uri
    ) {
        return MongoClients.create(uri);
    }

    @Bean(name = "segmentationMongoDatabaseFactory")
    public MongoDatabaseFactory segmentationMongoDatabaseFactory(
        @Qualifier("segmentationMongoClient") MongoClient mongoClient,
        @Value("${spring.data.mongodb.segmentation.uri}") String uri
    ) {
        String database = new ConnectionString(uri).getDatabase();
        if (database == null || database.isBlank()) {
            throw new IllegalArgumentException("spring.data.mongodb.segmentation.uri must include a database name");
        }
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Bean(name = "segmentationMongoTemplate")
    public MongoTemplate segmentationMongoTemplate(
        @Qualifier("segmentationMongoDatabaseFactory") MongoDatabaseFactory mongoDatabaseFactory
    ) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
