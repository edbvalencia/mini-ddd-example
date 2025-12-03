package com.alutarb.analytics.interactionscounter.application.increment;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.interactionscounter.domain.InteractionsCounter;
import com.alutarb.analytics.interactionscounter.domain.InteractionsCounterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InteractionsCounterSearcher {

    private final InteractionsCounterRepository repository;

    public InteractionsCounter search(String id) {
        return repository.search(id);
    }

}
