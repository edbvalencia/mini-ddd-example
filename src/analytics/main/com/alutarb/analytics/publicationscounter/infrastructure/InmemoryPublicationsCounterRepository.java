package com.alutarb.analytics.publicationscounter.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.alutarb.analytics.publicationscounter.domain.PublicationsCounter;
import com.alutarb.analytics.publicationscounter.domain.PublicationsCounterRepository;
import com.alutarb.shared.domain.Constants;

@Repository
public class InmemoryPublicationsCounterRepository implements PublicationsCounterRepository {

    private final Map<String, PublicationsCounter> storage = new ConcurrentHashMap<>();

    @Override
    public PublicationsCounter search(String id) {
        return storage.get(id);
    }

    @Override
    public void save(PublicationsCounter counter) {
        if (counter == null) {
            return;
        }
        storage.put(Constants.ID, counter);
    }
}
