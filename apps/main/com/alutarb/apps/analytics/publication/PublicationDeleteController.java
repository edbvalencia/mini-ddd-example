package com.alutarb.apps.analytics.publication;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.analytics.publication.domain.PublicationRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicationDeleteController {

    private final PublicationRepository publicationRepository;

    @DeleteMapping("/publications")
    public void delete() {
        publicationRepository.deleteAll();
    }

}
