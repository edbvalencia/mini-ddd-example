package com.alutarb.analytics.segmentationpublication.application.search;

import java.time.Instant;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.shared.domain.SocialNetwork;

public record SegmentationPublicationResponse(
    String id,
    Long audience,
    Long comments,
    Long interactions,
    Long reactions,
    Long shares,
    SocialNetwork socialNetwork,
    String text,
    Instant createdAt
) {

    public static SegmentationPublicationResponse of(SegmentationPublication publication) {
        return new SegmentationPublicationResponse(
            publication.id(),
            publication.audience(),
            publication.comments(),
            publication.interactions(),
            publication.reactions(),
            publication.shares(),
            publication.socialNetwork(),
            publication.text(),
            publication.createdAt()
        );
    }

}