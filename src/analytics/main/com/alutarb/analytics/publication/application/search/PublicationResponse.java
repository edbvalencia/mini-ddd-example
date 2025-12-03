package com.alutarb.analytics.publication.application.search;

import java.time.Instant;
import java.util.List;

import com.alutarb.analytics.publication.domain.Publication;
import com.alutarb.shared.domain.PublicationSentimentType;

public record PublicationResponse(
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

    public static List<PublicationResponse> from(List<Publication> publications) {
        return publications.stream()
            .map(PublicationResponse::from)
            .toList();
    }

    public static PublicationResponse from(Publication publication) {
        return new PublicationResponse(
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
        );
    }

}
