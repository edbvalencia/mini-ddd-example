package com.alutarb.analytics.segmentationpublication.application.create;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
    Instant createdAt,
    List<Double> embedding,
    String color,
    String emotion,
    String link,
    String media,
    String gobColor,
    String itemType,
    String page,
    String avatar,
    String dataType,
    String impactLevel,
    String platform,
    String reachLevel,
    Instant registeredAt,
    Map<String, Object> bigFive,
    String cleanText,
    Boolean isValid,
    String municipality,
    String subtopic,
    String summary,
    String title,
    String topic,
    Boolean validText
) {
}
