package com.alutarb.analytics.segmentationpublication.domain;

import java.time.Instant;
import java.util.List;

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
        List<Double> embedding
    ) {
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
        List<Double> embedding
    ) {
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
            embedding
        );
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

}
