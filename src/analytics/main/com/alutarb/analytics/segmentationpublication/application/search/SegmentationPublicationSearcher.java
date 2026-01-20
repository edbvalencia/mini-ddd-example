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
        return searchByQueryWithStats(query, limit, null, null, null, null);
    }

    public SegmentationPublicationsResponse searchByQueryWithStats(String query, int limit, Instant createdAtFrom,
        Instant createdAtTo) {
        return searchByQueryWithStats(query, limit, createdAtFrom, createdAtTo, null, null);
    }

    public SegmentationPublicationsResponse searchByQueryWithStats(
        String query,
        int limit,
        Instant createdAtFrom,
        Instant createdAtTo,
        Double scoreThreshold,
        String payloadFilterExpression
    ) {
        return searchByQueryWithStats(query, limit, createdAtFrom, createdAtTo, scoreThreshold,
            payloadFilterExpression, true);
    }

    public SegmentationPublicationsResponse searchByQueryWithStats(
        String query,
        int limit,
        Instant createdAtFrom,
        Instant createdAtTo,
        Double scoreThreshold,
        String payloadFilterExpression,
        boolean includeEmbeddings
    ) {
        var result = repository.searchByQueryWithStats(query, limit, createdAtFrom, createdAtTo, scoreThreshold,
            payloadFilterExpression);

        var publications = result.publications().stream()
            .map(publication -> {
                if (includeEmbeddings) {
                    return SegmentationPublicationResponse.of(publication);
                }
                return new SegmentationPublicationResponse(
                    publication.id(),
                    publication.audience(),
                    publication.comments(),
                    publication.interactions(),
                    publication.reactions(),
                    publication.shares(),
                    publication.socialNetwork(),
                    publication.text(),
                    publication.createdAt(),
                    null
                );
            })
            .toList();

        return new SegmentationPublicationsResponse(publications);
    }

}
