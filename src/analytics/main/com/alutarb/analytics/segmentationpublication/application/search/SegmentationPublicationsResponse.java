package com.alutarb.analytics.segmentationpublication.application.search;

import java.util.List;

public record SegmentationPublicationsResponse(
    List<SegmentationPublicationResponse> publications
) {

}
