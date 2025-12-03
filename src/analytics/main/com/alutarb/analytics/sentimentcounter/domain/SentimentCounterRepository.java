package com.alutarb.analytics.sentimentcounter.domain;

public interface SentimentCounterRepository {

    SentimentCounter search(String id);

    void save(SentimentCounter counter);

}
