package com.alutarb.analytics.shared.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;

public final class VectorStoreUtils {

    private VectorStoreUtils() {
    }

    public static String toUuid(String id) {
        return UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public static List<String> toUuids(List<String> ids) {
        return ids.stream().map(VectorStoreUtils::toUuid).toList();
    }

    public static String buildEmbedding(Map<String, Object> fields) {
        return String.join(" | ", fields.entrySet().stream()
            .filter(entry -> entry.getValue() != null && !entry.getValue().toString().isBlank())
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .toList());
    }

    public static Document buildDocument(String id, Map<String, Object> fields, Map<String, Object> metadata) {
        return new Document(toUuid(id), buildEmbedding(fields), metadata);
    }


}