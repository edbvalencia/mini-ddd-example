package com.alutarb.analytics.sentimentcounter.application.increment;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.sentimentcounter.domain.SentimentCounter;
import com.alutarb.analytics.sentimentcounter.domain.SentimentCounterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SentimentCounterSearcher {

    private final SentimentCounterRepository repository;

    public SentimentCounter search(String id) {
        return repository.search(id);
    }

}
