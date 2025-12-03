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
}
