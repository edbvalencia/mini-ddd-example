package com.alutarb.analytics.interactionscounter.application.increment;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.interactionscounter.domain.InteractionsCounter;
import com.alutarb.analytics.interactionscounter.domain.InteractionsCounterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InteractionsCounterIncrementer {

    private final InteractionsCounterSearcher searcher;
    private final InteractionsCounterRepository repository;

    public void increment(
        String id,
        String publicationId,
        int likesCount,
        int sharesCount,
        int viewsCount,
        int commentsCount
    ) {
        InteractionsCounter counter = searcher.search(id);
        if (counter == null) {
            counter = InteractionsCounter.initialize(id);
        }

        if (counter.alreadyCounted(publicationId)) return;

        counter.increment(publicationId, likesCount, sharesCount, viewsCount, commentsCount);
        repository.save(counter);
    }

}
