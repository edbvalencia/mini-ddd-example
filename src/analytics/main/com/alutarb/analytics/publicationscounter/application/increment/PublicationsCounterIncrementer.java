package com.alutarb.analytics.publicationscounter.application.increment;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.publicationscounter.application.search.PublicationsCounterSearcher;
import com.alutarb.analytics.publicationscounter.domain.PublicationsCounter;
import com.alutarb.analytics.publicationscounter.domain.PublicationsCounterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicationsCounterIncrementer {

    private final PublicationsCounterSearcher searcher;
    private final PublicationsCounterRepository repository;

    public void increment(String id, String publicationId) {
        PublicationsCounter counter = searcher.search(id);
        if (counter == null) {
            counter = PublicationsCounter.initialize(id);
        }

        if (counter.alreadyCounted(publicationId)) return;

        counter.increment(publicationId);
        repository.save(counter);
    }

}
