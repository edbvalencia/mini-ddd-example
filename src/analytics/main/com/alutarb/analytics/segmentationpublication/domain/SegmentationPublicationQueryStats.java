package com.alutarb.analytics.segmentationpublication.domain;

public record SegmentationPublicationQueryStats(
    Long audience,
    Long comments,
    Long interactions,
    Long reactions,
    Long shares,
    int publicationsCount
) {

    public static SegmentationPublicationQueryStats empty() {
        return new SegmentationPublicationQueryStats(0L, 0L, 0L, 0L, 0L, 0);
    }
}
