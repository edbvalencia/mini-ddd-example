package com.alutarb.analytics.segmentationpublication.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.alutarb.shared.domain.AggregateRoot;
import com.alutarb.shared.domain.SocialNetwork;

public class SegmentationPublication extends AggregateRoot {

    private String id;
    private Long audience;
    private Long comments;
    private Long interactions;
    private Long reactions;
    private Long shares;
    private SocialNetwork socialNetwork;
    private String text;
    private Instant createdAt;
    private List<Double> embedding;
    private String color;
    private String emotion;
    private String link;
    private String media;
    private String gobColor;
    private String itemType;
    private String page;
    private String avatar;
    private String dataType;
    private String impactLevel;
    private String platform;
    private String reachLevel;
    private Instant registeredAt;
    private Map<String, Object> bigFive;
    private String cleanText;
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
        Boolean validText) {
        this.id = id;
        this.audience = audience;
        this.comments = comments;
        this.interactions = interactions;
        this.reactions = reactions;
        this.shares = shares;
        this.socialNetwork = socialNetwork;
        this.text = text;
        this.createdAt = createdAt;
        this.embedding = embedding;
        this.color = color;
        this.emotion = emotion;
        this.link = link;
        this.media = media;
        this.gobColor = gobColor;
        this.itemType = itemType;
        this.page = page;
        this.avatar = avatar;
        this.dataType = dataType;
        this.impactLevel = impactLevel;
        this.platform = platform;
        this.reachLevel = reachLevel;
        this.registeredAt = registeredAt;
        this.bigFive = bigFive;
        this.cleanText = cleanText;
        this.isValid = isValid;
        this.municipality = municipality;
        this.subtopic = subtopic;
        this.summary = summary;
        this.title = title;
        this.topic = topic;
        this.validText = validText;
    }

    public static SegmentationPublication create(
        String id,
        Long audience,
        Long comments,
        Long interactions,
        Long reactions,
        Long shares,
        SocialNetwork socialNetwork,
        String text,
        Instant createdAt,
        List<Double> embedding) {
        return new SegmentationPublication(
            id,
            audience,
            comments,
            interactions,
            reactions,
            shares,
            socialNetwork,
            text,
            createdAt,
            embedding,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    }

    public String id() {
        return id;
    }

    public Long audience() {
        return audience;
    }

    public Long comments() {
        return comments;
    }

    public Long interactions() {
        return interactions;
    }

    public Long reactions() {
        return reactions;
    }

    public Long shares() {
        return shares;
    }

    public SocialNetwork socialNetwork() {
        return socialNetwork;
    }

    public String text() {
        return text;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public List<Double> embedding() {
        return embedding;
    }

    public String color() {
        return color;
    }

    public String emotion() {
        return emotion;
    }

    public String link() {
        return link;
    }

    public String media() {
        return media;
    }

    public String gobColor() {
        return gobColor;
    }

    public String itemType() {
        return itemType;
    }

    public String page() {
        return page;
    }

    public String avatar() {
        return avatar;
    }

    public String dataType() {
        return dataType;
    }

    public String impactLevel() {
        return impactLevel;
    }

    public String platform() {
        return platform;
    }

    public String reachLevel() {
        return reachLevel;
    }

    public Instant registeredAt() {
        return registeredAt;
    }

    public Map<String, Object> bigFive() {
        return bigFive;
    }

    public String cleanText() {
        return cleanText;
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
