package com.alutarb.analytics.interactionscounter.domain;

public interface InteractionsCounterRepository {

    InteractionsCounter search(String id);

    void save(InteractionsCounter counter);

}
