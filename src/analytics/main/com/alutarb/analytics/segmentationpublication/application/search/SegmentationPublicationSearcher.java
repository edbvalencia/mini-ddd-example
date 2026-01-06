package com.alutarb.analytics.segmentationpublication.application.search;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SegmentationPublicationSearcher {

    private final SegmentationPublicationRepository repository;

    public List<SegmentationPublication> searchByQuery(String query, int limit) {
        return repository.searchByQuery(query, limit, null, null);
    }

    public List<SegmentationPublication> searchByQuery(String query, int limit, Instant createdAtFrom,
        Instant createdAtTo) {
        return repository.searchByQuery(query, limit, createdAtFrom, createdAtTo);
    }

    public SegmentationPublicationsResponse searchByQueryWithStats(String query, int limit) {
        return searchByQueryWithStats(query, limit, null, null);
    }

    public SegmentationPublicationsResponse searchByQueryWithStats(String query, int limit, Instant createdAtFrom,
        Instant createdAtTo) {
        var result = repository.searchByQueryWithStats(query, limit, createdAtFrom, createdAtTo);
        var publications = result.publications().stream().map(SegmentationPublicationResponse::of).toList();
        var stats = result.stats();
        return new SegmentationPublicationsResponse(
            publications,
            stats.audience(),
            stats.comments(),
            stats.interactions(),
            stats.reactions(),
            stats.shares(),
            stats.publicationsCount()
        );
    }

}
