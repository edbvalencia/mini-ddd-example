package com.alutarb.analytics.publicationscounter.application.search;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.publicationscounter.domain.PublicationsCounter;
import com.alutarb.analytics.publicationscounter.domain.PublicationsCounterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicationsCounterSearcher {

    private final PublicationsCounterRepository repository;

    public PublicationsCounter search(String id) {
        return repository.search(id);
    }

}
