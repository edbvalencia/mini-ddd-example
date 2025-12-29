package com.alutarb.analytics.segmentationpublication.application;

import java.time.Instant;
import java.util.Map;

public record CreateSegmentationPublicationCommand(
    String id,
    Long audience,
    String avatar,
    Long comments,
    Instant createdAt,
    String dataType,
    String impactLevel,
    Long interactions,
    String itemType,
    String link,
    String media,
    String network,
    String page,
    String platform,
    String reachLevel,
    Long reactions,
    Instant registeredAt,
    Long shares,
    String text,
    Map<String, Object> bigFive,
    String cleanText,
    String color,
    String emotion,
    String gobColor,
    Boolean isValid,
    String municipality,
    String subtopic,
    String summary,
    String title,
    String topic,
    Boolean validText
) {
}
