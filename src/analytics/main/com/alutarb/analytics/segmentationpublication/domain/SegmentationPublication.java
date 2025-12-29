package com.alutarb.analytics.segmentationpublication.domain;

import java.time.Instant;
import java.util.Map;

import com.alutarb.shared.domain.AggregateRoot;

public class SegmentationPublication extends AggregateRoot {

    private String id;
    private Long audience;
    private String avatar;
    private Long comments;
    private Instant createdAt;
    private String dataType;
    private String impactLevel;
    private Long interactions;
    private String itemType;
    private String link;
    private String media;
    private String network;
    private String page;
    private String platform;
    private String reachLevel;
    private Long reactions;
    private Instant registeredAt;
    private Long shares;
    private String text;
    private Map<String, Object> bigFive;
    private String cleanText;
    private String color;
    private String emotion;
    private String gobColor;
    private Boolean isValid;
    private String municipality;
    private String subtopic;
    private String summary;
    private String title;
    private String topic;
    private Boolean validText;

    public SegmentationPublication(
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
        this.id = id;
        this.audience = audience;
        this.avatar = avatar;
        this.comments = comments;
        this.createdAt = createdAt;
        this.dataType = dataType;
        this.impactLevel = impactLevel;
        this.interactions = interactions;
        this.itemType = itemType;
        this.link = link;
        this.media = media;
        this.network = network;
        this.page = page;
        this.platform = platform;
        this.reachLevel = reachLevel;
        this.reactions = reactions;
        this.registeredAt = registeredAt;
        this.shares = shares;
        this.text = text;
        this.bigFive = bigFive;
        this.cleanText = cleanText;
        this.color = color;
        this.emotion = emotion;
        this.gobColor = gobColor;
        this.isValid = isValid;
        this.municipality = municipality;
        this.subtopic = subtopic;
        this.summary = summary;
        this.title = title;
        this.topic = topic;
        this.validText = validText;
    }

    public String id() {
        return id;
    }

    public Long audience() {
        return audience;
    }

    public String avatar() {
        return avatar;
    }

    public Long comments() {
        return comments;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public String dataType() {
        return dataType;
    }

    public String impactLevel() {
        return impactLevel;
    }

    public Long interactions() {
        return interactions;
    }

    public String itemType() {
        return itemType;
    }

    public String link() {
        return link;
    }

    public String media() {
        return media;
    }

    public String network() {
        return network;
    }

    public String page() {
        return page;
    }

    public String platform() {
        return platform;
    }

    public String reachLevel() {
        return reachLevel;
    }

    public Long reactions() {
        return reactions;
    }

    public Instant registeredAt() {
        return registeredAt;
    }

    public Long shares() {
        return shares;
    }

    public String text() {
        return text;
    }

    public Map<String, Object> bigFive() {
        return bigFive;
    }

    public String cleanText() {
        return cleanText;
    }

    public String color() {
        return color;
    }

    public String emotion() {
        return emotion;
    }

    public String gobColor() {
        return gobColor;
    }

    public Boolean isValid() {
        return isValid;
    }

    public String municipality() {
        return municipality;
    }

    public String subtopic() {
        return subtopic;
    }

    public String summary() {
        return summary;
    }

    public String title() {
        return title;
    }

    public String topic() {
        return topic;
    }

    public Boolean validText() {
        return validText;
    }
}
