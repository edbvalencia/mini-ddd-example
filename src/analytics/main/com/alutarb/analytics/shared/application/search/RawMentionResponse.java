package com.alutarb.analytics.shared.application.search;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RawMentionResponse(
    String id,
    String text,
    String link,
    String itemType,
    Long interactions,
    Long audience,
    Long reactions,
    Long comments,
    Long shares,
    String socialNetwork,
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
    String topic,
    String title,
    String summary,
    Boolean validText
) {

    public static RawMentionResponse of(com.alutarb.analytics.shared.domain.RawMention raw, List<Double> embedding) {
        return new RawMentionResponse(
            raw.id(),
            raw.text(),
            raw.link(),
            raw.itemType(),
            raw.interactions(),
            raw.audience(),
            raw.reactions(),
            raw.comments(),
            raw.shares(),
            raw.network(),
            raw.createdAt(),
            embedding,
            raw.color(),
            raw.emotion(),
            raw.media(),
            raw.gobColor(),
            raw.page(),
            raw.avatar(),
            raw.dataType(),
            raw.impactLevel(),
            raw.platform(),
            raw.reachLevel(),
            raw.registeredAt(),
            raw.bigFive(),
            raw.cleanText(),
            raw.isValid(),
            raw.municipality(),
            raw.subtopic(),
            raw.topic(),
            raw.title(),
            raw.summary(),
            raw.validText()
        );
    }

    public static RawMentionResponse of(com.alutarb.analytics.shared.domain.RawMention raw, List<Double> embedding,
        Set<String> fields) {
        Set<String> f = fields != null ? fields : Set.of();
        return new RawMentionResponse(
            raw.id(),
            raw.text(),
            raw.link(),
            raw.itemType(),
            raw.interactions(),
            raw.audience(),
            raw.reactions(),
            raw.comments(),
            f.contains("shares") ? raw.shares() : null,
            f.contains("socialNetwork") ? raw.network() : null,
            f.contains("createdAt") ? raw.createdAt() : null,
            embedding,
            f.contains("color") ? raw.color() : null,
            f.contains("emotion") ? raw.emotion() : null,
            f.contains("media") ? raw.media() : null,
            f.contains("gobColor") ? raw.gobColor() : null,
            f.contains("page") ? raw.page() : null,
            f.contains("avatar") ? raw.avatar() : null,
            f.contains("dataType") ? raw.dataType() : null,
            f.contains("impactLevel") ? raw.impactLevel() : null,
            f.contains("platform") ? raw.platform() : null,
            f.contains("reachLevel") ? raw.reachLevel() : null,
            f.contains("registeredAt") ? raw.registeredAt() : null,
            f.contains("bigFive") ? raw.bigFive() : null,
            f.contains("cleanText") ? raw.cleanText() : null,
            f.contains("isValid") ? raw.isValid() : null,
            f.contains("municipality") ? raw.municipality() : null,
            f.contains("subtopic") ? raw.subtopic() : null,
            f.contains("summary") ? raw.summary() : null,
            f.contains("title") ? raw.title() : null,
            f.contains("topic") ? raw.topic() : null,
            f.contains("validText") ? raw.validText() : null
        );
    }
}
