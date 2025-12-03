package com.alutarb.analytics.sentimentcounter.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.alutarb.analytics.sentimentcounter.domain.SentimentCounter;
import com.alutarb.analytics.sentimentcounter.domain.SentimentCounterRepository;
import com.alutarb.shared.domain.Constants;

@Repository
public class InmemorySentimentCounterRepository implements SentimentCounterRepository {

    private final Map<String, SentimentCounter> storage = new ConcurrentHashMap<>();

    @Override
    public SentimentCounter search(String id) {
        return storage.get(id);
    }

    @Override
    public void save(SentimentCounter counter) {
        if (counter == null) {
            return;
        }
        storage.put(Constants.ID, counter);
    }
}
