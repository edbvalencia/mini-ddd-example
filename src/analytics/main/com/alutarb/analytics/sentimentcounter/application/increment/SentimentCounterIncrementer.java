package com.alutarb.analytics.sentimentcounter.application.increment;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.sentimentcounter.domain.SentimentCounter;
import com.alutarb.analytics.sentimentcounter.domain.SentimentCounterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SentimentCounterIncrementer {

    private final SentimentCounterSearcher searcher;
    private final SentimentCounterRepository repository;

    public void increment(String id, String publicationId, int positiveCount, int negativeCount, int neutralCount) {
        SentimentCounter counter = searcher.search(id);
        if (counter == null) {
            counter = SentimentCounter.initialize(id);
        }

        if (counter.alreadyCounted(publicationId)) return;

        counter.increment(publicationId, positiveCount, negativeCount, neutralCount);
        repository.save(counter);
    }

}
