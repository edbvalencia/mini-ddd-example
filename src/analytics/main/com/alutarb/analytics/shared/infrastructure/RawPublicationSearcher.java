package com.alutarb.analytics.shared.infrastructure;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alutarb.analytics.shared.domain.RawPublication;
import com.alutarb.shared.domain.PublicationSentimentType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawPublicationSearcher {

    private final JdbcTemplate jdbc;

    public List<RawPublication> search(int page, int size) {
        try {
            int offset = page * size;

            String sqlIds = """
                SELECT q.post
                FROM salert_q3 q
                JOIN salert_post p ON p.id = q.post
                WHERE q.query = 307
                ORDER BY p.created_at DESC
                LIMIT ? OFFSET ?
                """;

            List<String> ids = jdbc.queryForList(sqlIds, String.class, size, offset);
            if (ids.isEmpty()) return List.of();

            String placeholders = String.join(",", ids.stream().map(id -> "?").toList());

            String sqlPosts = """
                SELECT
                    id,
                    text,
                    likes,
                    shares,
                    views,
                    comments,
                    sentiment,
                    hashtags,
                    mentions,
                    emojis,
                    concepts,
                    verbs,
                    created_at
                FROM salert_post
                WHERE id IN (""" + placeholders + ")";

            return jdbc.query(
                sqlPosts,
                (rs, rowNum) -> new RawPublication(
                    rs.getString("id"),
                    rs.getString("text"),
                    rs.getInt("likes"),
                    rs.getInt("shares"),
                    rs.getInt("views"),
                    rs.getInt("comments"),
                    parseSentiment(rs.getInt("sentiment")),
                    parseList(rs.getString("hashtags")),
                    parseList(rs.getString("mentions")),
                    parseList(rs.getString("emojis")),
                    parseList(rs.getString("concepts")),
                    parseList(rs.getString("verbs")),
                    rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toInstant() : null
                ),
                ids.toArray()
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> parseList(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return List.of(raw.split(","));
    }

    private PublicationSentimentType parseSentiment(Integer raw) {
        if (raw == null) return PublicationSentimentType.NEUTRAL;
        return switch (raw) {
            case -1 -> PublicationSentimentType.NEGATIVE;
            case 1 -> PublicationSentimentType.POSITIVE;
            default -> PublicationSentimentType.NEUTRAL;
        };
    }

}
