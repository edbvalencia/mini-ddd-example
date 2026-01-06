package com.alutarb.analytics.segmentationpublication.application.search;

import java.util.List;

public record SegmentationPublicationsResponse(
    List<SegmentationPublicationResponse> publications,
    Long audience,
    Long comments,
    Long interactions,
    Long reactions,
    Long shares,
    int publicationsCount
) {

}
