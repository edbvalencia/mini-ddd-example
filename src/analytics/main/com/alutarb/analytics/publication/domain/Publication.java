package com.alutarb.analytics.publication.domain;

import java.time.Instant;
import java.util.List;

import com.alutarb.shared.domain.AggregateRoot;
import com.alutarb.shared.domain.PublicationCreatedDomainEvent;
import com.alutarb.shared.domain.PublicationSentimentType;

public class Publication extends AggregateRoot {

    private String id;
    private String text;
    private Integer likes;
    private Integer shares;
    private Integer views;
    private Integer comments;
    private PublicationSentimentType sentiment;
    private List<String> hashtags;
    private List<String> mentions;
    private List<String> emojis;
    private List<String> concepts;
    private List<String> verbs;
    private Instant createdAt;

    public Publication(
        String id,
        String text,
        List<String> hashtags,
        List<String> mentions,
        List<String> emojis,
        List<String> concepts,
        List<String> verbs,
        Integer likes,
        Integer shares,
        Integer views,
        Integer comments,
        PublicationSentimentType sentiment,
        Instant createdAt
    ) {
        this.id = id;
        this.text = text;
        this.hashtags = hashtags;
        this.mentions = mentions;
        this.emojis = emojis;
        this.concepts = concepts;
        this.verbs = verbs;
        this.likes = likes;
        this.shares = shares;
        this.views = views;
        this.comments = comments;
        this.sentiment = sentiment;
        this.createdAt = createdAt;
    }

    public static Publication create(
        String id,
        String text,
        List<String> hashtags,
        List<String> mentions,
        List<String> emojis,
        List<String> concepts,
        List<String> verbs,
        Integer likes,
        Integer shares,
        Integer views,
        Integer comments,
        PublicationSentimentType sentiment,
        Instant createdAt
    ) {
        var publication = new Publication(
            id,
            text,
            hashtags,
            mentions,
            emojis,
            concepts,
            verbs,
            likes,
            shares,
            views,
            comments,
            sentiment,
            createdAt
        );

        publication.record(new PublicationCreatedDomainEvent(
            publication.id(),
            publication.text(),
            publication.likes(),
            publication.shares(),
            publication.views(),
            publication.comments(),
            publication.sentiment(),
            publication.hashtags(),
            publication.mentions(),
            publication.emojis(),
            publication.concepts(),
            publication.verbs(),
            publication.createdAt()
        ));

        return publication;
    }

    public String id() {
        return id;
    }

    public String text() {
        return text;
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

    public Instant createdAt() {
        return createdAt;
    }

}
