package com.alutarb.analytics.publication.application.search;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.publication.domain.PublicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicationSearcher {

    private final PublicationRepository repository;

    public List<PublicationResponse> searchByQuery(String query, int limit) {
        return PublicationResponse.from(repository.searchByQuery(query, limit));
    }

}
