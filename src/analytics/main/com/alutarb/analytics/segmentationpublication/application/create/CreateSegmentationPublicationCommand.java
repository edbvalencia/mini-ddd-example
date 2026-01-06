package com.alutarb.analytics.segmentationpublication.application.create;

import java.time.Instant;

import com.alutarb.shared.domain.SocialNetwork;

public record CreateSegmentationPublicationCommand(
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
}
