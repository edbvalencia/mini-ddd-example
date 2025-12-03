package com.alutarb.shared.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import com.alutarb.shared.domain.bus.event.DomainEvent;

public class PublicationCreatedDomainEvent extends DomainEvent {

    private final String text;
    private final Integer likes;
    private final Integer shares;
    private final Integer views;
    private final Integer comments;
    private final PublicationSentimentType sentiment;
    private final List<String> hashtags;
    private final List<String> mentions;
    private final List<String> emojis;
    private final List<String> concepts;
    private final List<String> verbs;
    private final Instant createdAt;

    public PublicationCreatedDomainEvent(
        String id,
        String text,
        Integer likes,
        Integer shares,
        Integer views,
        Integer comments,
        PublicationSentimentType sentiment,
        List<String> hashtags,
        List<String> mentions,
        List<String> emojis,
        List<String> concepts,
        List<String> verbs,
        Instant createdAt
    ) {
        super(id);
        this.text = text;
        this.likes = likes;
        this.shares = shares;
        this.views = views;
        this.comments = comments;
        this.sentiment = sentiment;
        this.hashtags = hashtags;
        this.mentions = mentions;
        this.emojis = emojis;
        this.concepts = concepts;
        this.verbs = verbs;
        this.createdAt = createdAt;
    }

    public PublicationCreatedDomainEvent(
        String id,
        String eventId,
        String occurredOn,
        String text,
        Integer likes,
        Integer shares,
        Integer views,
        Integer comments,
        PublicationSentimentType sentiment,
        List<String> hashtags,
        List<String> mentions,
        List<String> emojis,
        List<String> concepts,
        List<String> verbs,
        Instant createdAt
    ) {
        super(id, eventId, occurredOn);
        this.text = text;
        this.likes = likes;
        this.shares = shares;
        this.views = views;
        this.comments = comments;
        this.sentiment = sentiment;
        this.hashtags = hashtags;
        this.mentions = mentions;
        this.emojis = emojis;
        this.concepts = concepts;
        this.verbs = verbs;
        this.createdAt = createdAt;
    }

    @Override
    public String eventName() {
        return "publication.created";
    }

    @Override
    public HashMap<String, Serializable> toPrimitives() {
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("text", text);
        map.put("likes", likes);
        map.put("shares", shares);
        map.put("views", views);
        map.put("comments", comments);
        map.put("sentiment", sentiment != null ? sentiment.name() : null);
        map.put("hashtags", (Serializable) hashtags);
        map.put("mentions", (Serializable) mentions);
        map.put("emojis", (Serializable) emojis);
        map.put("concepts", (Serializable) concepts);
        map.put("verbs", (Serializable) verbs);
        map.put("createdAt", createdAt != null ? createdAt.toString() : null);
        return map;
    }

    @Override
    public DomainEvent fromPrimitives(
        String aggregateId,
        HashMap<String, Serializable> body,
        String eventId,
        String occurredOn
    ) {
        return new PublicationCreatedDomainEvent(
            aggregateId,
            eventId,
            occurredOn,
            (String) body.get("text"),
            (Integer) body.get("likes"),
            (Integer) body.get("shares"),
            (Integer) body.get("views"),
            (Integer) body.get("comments"),
            body.get("sentiment") != null ? PublicationSentimentType.valueOf((String) body.get("sentiment")) : null,
            (List<String>) body.get("hashtags"),
            (List<String>) body.get("mentions"),
            (List<String>) body.get("emojis"),
            (List<String>) body.get("concepts"),
            (List<String>) body.get("verbs"),
            body.get("createdAt") != null ? Instant.parse((String) body.get("createdAt")) : null
        );
    }

    public String text() {
        return text;
    }

    public Integer likes() {
        return likes;
    }

    public Integer shares() {
        return shares;
    }

    public Integer views() {
        return views;
    }

    public Integer comments() {
        return comments;
    }

    public PublicationSentimentType sentiment() {
        return sentiment;
    }

    public List<String> hashtags() {
        return hashtags;
    }

    public List<String> mentions() {
        return mentions;
    }

    public List<String> emojis() {
        return emojis;
    }

    public List<String> concepts() {
        return concepts;
    }

    public List<String> verbs() {
        return verbs;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
