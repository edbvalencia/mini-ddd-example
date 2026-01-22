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
        System.out.println("[QUERY-SEARCH] Query: " + query);
        System.out.println("[QUERY-SEARCH] Date filter: " + buildDateFilter(createdAtFrom, createdAtTo));

        var embeddingsById = qdrantRepository.searchWithEmbeddings(
            query, limit, createdAtFrom, createdAtTo, scoreThreshold, payloadFilterExpression
        );

        System.out.println("[QUERY-SEARCH] Found " + embeddingsById.size() + " results from Qdrant");

        if (embeddingsById.isEmpty()) {
            return new RawMentionsResponse(List.of());
        }

        List<String> ids = List.copyOf(embeddingsById.keySet());
        List<RawMention> rawMentions = rawSearcher.searchByIds(ids);

        System.out.println("[QUERY-SEARCH] Found " + rawMentions.size() + " RawMentions from MongoDB");

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
        // Use fixed dates if null parameters are provided
        Instant actualFrom = (from != null) ? from : java.time.LocalDate.of(2025, 11, 1).atStartOfDay(
            java.time.ZoneOffset.UTC).toInstant();
        Instant actualTo = (to != null) ? to : java.time.LocalDate.of(2026, 1, 24).atStartOfDay(
            java.time.ZoneOffset.UTC).toInstant();

        Long fromSec = actualFrom.getEpochSecond();
        Long toSec = actualTo.getEpochSecond();

        return "createdAtSec >= " + fromSec + " && createdAtSec <= " + toSec;
    }
}
