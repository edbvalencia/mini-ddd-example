package com.alutarb.analytics.segmentationpublication.domain;

import java.util.List;

public record SegmentationPublicationSearchResult(
    List<SegmentationPublication> publications,
    SegmentationPublicationQueryStats stats
) {
}
