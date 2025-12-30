package com.alutarb.analytics.segmentationpublication.infrastructure;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublicationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MongoSegmentationPublicationRepository implements SegmentationPublicationRepository {

    private final MongoTemplate template;
    private final QdrantSegmentationPublicationRepository qdrantRepository;

    @Override
    public List<SegmentationPublication> searchByQuery(String query, int limit) {
        return qdrantRepository.searchIdsByQuery(query, limit).stream()
            .map(id -> template.findById(id, SegmentationPublication.class))
            .toList();
    }

    @Override
    public void save(SegmentationPublication publication) {
        template.save(publication);
        qdrantRepository.save(publication);
    }

}