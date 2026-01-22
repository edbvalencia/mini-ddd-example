package com.alutarb.analytics.shared.application.search;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.shared.domain.RawMention;
import com.alutarb.analytics.shared.infrastructure.RawMentionQdrantRepository;
import com.alutarb.analytics.shared.infrastructure.RawSegmentationPublicationSearcher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawMentionSearcher {

    private final RawSegmentationPublicationSearcher rawSearcher;
    private final RawMentionQdrantRepository qdrantRepository;

    public RawMentionsResponse searchByQueryWithStats(
        String query,
        int limit,
        Instant createdAtFrom,
        Instant createdAtTo,
        Double scoreThreshold,
        String payloadFilterExpression,
        boolean includeEmbeddings,
        Set<String> fields
    ) {
        // Get embeddings from Qdrant
        var embeddingsById = qdrantRepository.searchWithEmbeddings(
            query, limit, createdAtFrom, createdAtTo, scoreThreshold, payloadFilterExpression
        );

        if (embeddingsById.isEmpty()) {
            return new RawMentionsResponse(List.of());
        }

        List<String> ids = List.copyOf(embeddingsById.keySet());
        List<RawMention> rawMentions = rawSearcher.searchByIds(ids);

        List<RawMentionResponse> responses = rawMentions.stream()
            .map(raw -> {
                List<Double> embedding = includeEmbeddings ? embeddingsById.get(raw.id()) : null;
                return RawMentionResponse.of(raw, embedding, fields);
            })
            .toList();

        return new RawMentionsResponse(responses);
    }

    public List<RawMentionResponse> searchByDateRange(
        Instant from,
        Instant to,
        int limit,
        Set<String> fields
    ) {
        List<RawMention> rawMentions = rawSearcher.searchByDateRange(from, to, 0, limit);

        return rawMentions.stream()
            .map(raw -> RawMentionResponse.of(raw, null, fields))
            .toList();
    }

    private String buildDateFilter(Instant from, Instant to) {
        if (from == null && to == null) {
            return null;
        }

        Long fromSec = from != null ? from.getEpochSecond() : null;
        Long toSec = to != null ? to.getEpochSecond() : null;

        if (fromSec != null && toSec != null) {
            return "createdAtSec >= " + fromSec + " && createdAtSec <= " + toSec;
        }
        if (fromSec != null) {
            return "createdAtSec >= " + fromSec;
        }
        return "createdAtSec <= " + toSec;
    }
}
