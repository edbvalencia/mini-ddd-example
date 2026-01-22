package com.alutarb.apps.analytics.publication;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.analytics.shared.application.search.RawMentionResponse;
import com.alutarb.analytics.shared.application.search.RawMentionSearcher;
import com.alutarb.analytics.shared.application.search.RawMentionsResponse;
import com.alutarb.analytics.shared.infrastructure.RawSegmentationPublicationSearcher;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicationGetController {

    private final RawMentionSearcher rawMentionSearcher;

    @GetMapping("/segmentation-publications")
    public RawMentionsResponse search(
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
        return rawMentionSearcher.searchByQueryWithStats(query, topK, from, to, threshold,
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
    public List<RawMentionResponse> searchByDateRange(
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(defaultValue = "100") int limit,
        @RequestParam(required = false) Set<String> fields
    ) {
        return rawMentionSearcher.searchByDateRange(from, to, limit, fields);
    }

}
