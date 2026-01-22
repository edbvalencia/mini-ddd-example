package com.alutarb.analytics.segmentationpublication.application.search;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.shared.domain.SocialNetwork;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SegmentationPublicationResponse(
    String id,
    String text,
    String link,
    String itemType,
    Long interactions,
    Long audience,
    Long reactions,
    Long comments,
    Long shares,
    SocialNetwork socialNetwork,
    Instant createdAt,
    List<Double> embedding,
    String color,
    String emotion,
    String media,
    String gobColor,
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

    public static SegmentationPublicationResponse of(SegmentationPublication publication) {
        return new SegmentationPublicationResponse(
            publication.id(),
            publication.text(),
            publication.link(),
            publication.itemType(),
            publication.interactions(),
            publication.audience(),
            publication.reactions(),
            publication.comments(),
            publication.shares(),
            publication.socialNetwork(),
            publication.createdAt(),
            publication.embedding(),
            publication.color(),
            publication.emotion(),
            publication.media(),
            publication.gobColor(),
            publication.page(),
            publication.avatar(),
            publication.dataType(),
            publication.impactLevel(),
            publication.platform(),
            publication.reachLevel(),
            publication.registeredAt(),
            publication.bigFive(),
            publication.cleanText(),
            publication.isValid(),
            publication.municipality(),
            publication.subtopic(),
            publication.summary(),
            publication.title(),
            publication.topic(),
            publication.validText()
        );
    }

}