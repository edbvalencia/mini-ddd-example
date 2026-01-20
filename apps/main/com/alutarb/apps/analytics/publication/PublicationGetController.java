package com.alutarb.apps.analytics.publication;

import java.time.Instant;

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
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Instant from,
        @RequestParam(required = false) Instant to,
        @RequestParam(required = false) Double scoreThreshold,
        @RequestParam(required = false) String payloadFilterExpression,
        @RequestParam(defaultValue = "false") boolean includeEmbeddings
    ) {
        return segmentationSearcher.searchByQueryWithStats(query, size, from, to, scoreThreshold,
            payloadFilterExpression, includeEmbeddings);
    }

}
