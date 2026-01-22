package com.alutarb.analytics.shared.application.search;

import java.util.List;

public record RawMentionsResponse(
    List<RawMentionResponse> publications
) {
}
