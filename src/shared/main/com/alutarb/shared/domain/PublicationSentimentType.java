package com.alutarb.shared.domain;

public enum PublicationSentimentType {
    POSITIVE,
    NEGATIVE,
    NEUTRAL;

    public boolean isPositive() {
        return this == POSITIVE;
    }

    public boolean isNegative() {
        return this == NEGATIVE;
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
    }

    public static PublicationSentimentType from(String name) {
        return valueOf(name);
    }

    public static PublicationSentimentType from(int value) {
        return values()[value];
    }
}
