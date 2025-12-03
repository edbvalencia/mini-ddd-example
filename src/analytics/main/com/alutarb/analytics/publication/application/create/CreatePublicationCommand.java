package com.alutarb.analytics.publication.application.create;

import java.time.Instant;
import java.util.List;

import com.alutarb.shared.domain.PublicationSentimentType;

public record CreatePublicationCommand(
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

}
