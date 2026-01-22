package com.alutarb.analytics.segmentationpublication.application.search;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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
        return searchByQueryWithStats(query, limit, createdAtFrom, createdAtTo, scoreThreshold,
            payloadFilterExpression, includeEmbeddings, null);
    }

    public SegmentationPublicationsResponse searchByQueryWithStats(
        String query,
        int limit,
        Instant createdAtFrom,
        Instant createdAtTo,
        Double scoreThreshold,
        String payloadFilterExpression,
        boolean includeEmbeddings,
        Set<String> fields
    ) {
        var result = repository.searchByQueryWithStats(query, limit, createdAtFrom, createdAtTo, scoreThreshold,
            payloadFilterExpression);

        var publications = result.publications().stream()
            .map(publication -> mapToResponse(publication, includeEmbeddings, fields))
            .toList();

        return new SegmentationPublicationsResponse(publications);
    }

    private SegmentationPublicationResponse mapToResponse(
        SegmentationPublication publication,
        boolean includeEmbeddings,
        Set<String> fields
    ) {
        Set<String> f = fields != null ? fields : Set.of();
        return new SegmentationPublicationResponse(
            publication.id(),
            publication.text(),
            publication.link(),
            publication.itemType(),
            publication.interactions(),
            publication.audience(),
            publication.reactions(),
            publication.comments(),
            f.contains("shares") ? publication.shares() : null,
            f.contains("socialNetwork") ? publication.socialNetwork() : null,
            f.contains("createdAt") ? publication.createdAt() : null,
            includeEmbeddings ? publication.embedding() : null,
            f.contains("color") ? publication.color() : null,
            f.contains("emotion") ? publication.emotion() : null,
            f.contains("media") ? publication.media() : null,
            f.contains("gobColor") ? publication.gobColor() : null,
            f.contains("page") ? publication.page() : null,
            f.contains("avatar") ? publication.avatar() : null,
            f.contains("dataType") ? publication.dataType() : null,
            f.contains("impactLevel") ? publication.impactLevel() : null,
            f.contains("platform") ? publication.platform() : null,
            f.contains("reachLevel") ? publication.reachLevel() : null,
            f.contains("registeredAt") ? publication.registeredAt() : null,
            f.contains("bigFive") ? publication.bigFive() : null,
            f.contains("cleanText") ? publication.cleanText() : null,
            f.contains("isValid") ? publication.isValid() : null,
            f.contains("municipality") ? publication.municipality() : null,
            f.contains("subtopic") ? publication.subtopic() : null,
            f.contains("summary") ? publication.summary() : null,
            f.contains("title") ? publication.title() : null,
            f.contains("topic") ? publication.topic() : null,
            f.contains("validText") ? publication.validText() : null
        );
    }

}
