package com.alutarb.apps.analytics.publication;

import java.time.Instant;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.analytics.segmentationpublication.application.search.SegmentationPublicationSearcher;
import com.alutarb.analytics.segmentationpublication.application.search.SegmentationPublicationsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicationGetController {

    private final SegmentationPublicationSearcher segmentationSearcher;

    @GetMapping("/segmentation-publications")
    public SegmentationPublicationsResponse search(
        @RequestParam String query,
        @RequestParam(defaultValue = "10") int topK,
        @RequestParam(required = false) Double threshold,
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(required = false) String color,
        @RequestParam(required = false) String payloadFilterExpression,
        @RequestParam(defaultValue = "false") boolean withEmbeddings,
        @RequestParam(required = false) Set<String> fields
    ) {
        String filter = buildFilterExpression(color, payloadFilterExpression);
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

}
