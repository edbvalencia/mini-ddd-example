package com.alutarb.analytics.sentimentcounter.domain;

import java.util.ArrayList;
import java.util.List;

public class SentimentCounter {

    private String id;
    private List<String> publicationsIds;
    private int positiveCount;
    private int negativeCount;
    private int neutralCount;

    public SentimentCounter(
        String id,
        List<String> publicationsIds,
        int positiveCount,
        int negativeCount,
        int neutralCount
    ) {
        this.id = id;
        this.publicationsIds = publicationsIds;
        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
        this.neutralCount = neutralCount;
    }

    public static SentimentCounter initialize(String id) {
        return new SentimentCounter(id, new ArrayList<>(), 0, 0, 0);
    }

    public void increment(String publicationId, int positiveCount, int negativeCount, int neutralCount) {
        this.publicationsIds.add(publicationId);
        this.positiveCount += positiveCount;
        this.negativeCount += negativeCount;
        this.neutralCount += neutralCount;
    }

    public boolean alreadyCounted(String publicationId) {
        return this.publicationsIds.contains(publicationId);
    }

    public int positiveCount() {
        return positiveCount;
    }

    public int negativeCount() {
        return negativeCount;
    }

    public int neutralCount() {
        return neutralCount;
    }
}
