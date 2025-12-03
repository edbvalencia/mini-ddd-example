package com.alutarb.analytics.interactionscounter.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.alutarb.analytics.interactionscounter.domain.InteractionsCounter;
import com.alutarb.analytics.interactionscounter.domain.InteractionsCounterRepository;
import com.alutarb.shared.domain.Constants;

@Repository
public class InmemoryInteractionsCounterRepository implements InteractionsCounterRepository {

    private final Map<String, InteractionsCounter> storage = new ConcurrentHashMap<>();

    @Override
    public InteractionsCounter search(String id) {
        return storage.get(id);
    }

    @Override
    public void save(InteractionsCounter counter) {
        if (counter == null) {
            return;
        }
        storage.put(Constants.ID, counter);
    }
}
