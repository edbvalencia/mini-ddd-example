package com.alutarb.apps.analytics.publication;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.analytics.segmentationpublication.application.search.SegmentationPublicationResponse;
import com.alutarb.analytics.segmentationpublication.application.search.SegmentationPublicationSearcher;
import com.alutarb.analytics.segmentationpublication.application.search.SegmentationPublicationsResponse;
import com.alutarb.analytics.segmentationpublication.infrastructure.MongoSegmentationPublicationRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicationGetController {

    private final SegmentationPublicationSearcher segmentationSearcher;
    private final MongoSegmentationPublicationRepository mongoRepository;

    @GetMapping("/segmentation-publications")
    public SegmentationPublicationsResponse search(
        @RequestParam String query,
        @RequestParam(defaultValue = "10") int topK,
        @RequestParam(required = false) Double threshold,
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(required = false) String color,
        @RequestParam(required = false) String payloadFilter,
        @RequestParam(defaultValue = "false") boolean withEmbeddings,
        @RequestParam(required = false) Set<String> fields
    ) {
        String filter = buildFilterExpression(color, payloadFilter);
        return segmentationSearcher.searchByQueryWithStats(query, topK, from, to, threshold,
            filter, withEmbeddings, fields);
    }

    private String buildFilterExpression(String color, String payloadFilterExpression) {
        String colorFilter = color != null && !color.isBlank() ? "color == '" + color + "'" : null;

        if (colorFilter != null && payloadFilterExpression != null && !payloadFilterExpression.isBlank()) {
            return colorFilter + " && " + payloadFilterExpression;
        }
        if (colorFilter != null) {
            return colorFilter;
        }
        return payloadFilterExpression;
    }

    @GetMapping("/segmentation-publications/by-date")
    public List<SegmentationPublicationResponse> searchByDateRange(
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(defaultValue = "100") int limit,
        @RequestParam(required = false) Set<String> fields
    ) {
        Set<String> f = fields != null ? fields : Set.of();
        return mongoRepository.searchByDateRange(from, to, limit).stream()
            .map(pub -> mapToResponse(pub, f))
            .toList();
    }

    private SegmentationPublicationResponse mapToResponse(
        com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication publication,
        Set<String> f
    ) {
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
            null,
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
