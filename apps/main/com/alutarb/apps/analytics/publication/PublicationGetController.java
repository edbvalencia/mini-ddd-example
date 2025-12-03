package com.alutarb.apps.analytics.publication;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.analytics.publication.application.search.PublicationResponse;
import com.alutarb.analytics.publication.application.search.PublicationSearcher;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicationGetController {

    private final PublicationSearcher searcher;

    @GetMapping("/publications")
    public List<PublicationResponse> search(@RequestParam String query, @RequestParam(defaultValue = "10") int size) {
        return searcher.searchByQuery(query, size);
    }

}
