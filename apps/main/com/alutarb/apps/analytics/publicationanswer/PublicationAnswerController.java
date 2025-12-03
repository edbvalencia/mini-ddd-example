package com.alutarb.apps.analytics.publicationanswer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.apps.analytics.publication.PublicationsQuestionAnswerer;
import com.alutarb.apps.analytics.publication.PublicationsQuestionAnswerer.ChatResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicationAnswerController {

    private final PublicationsQuestionAnswerer answerer;

    @GetMapping("/publication-answers")
    public ChatResponse chat(@RequestParam String question) {
        return answerer.ask(question);
    }

}