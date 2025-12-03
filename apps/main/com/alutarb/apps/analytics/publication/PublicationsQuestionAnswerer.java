package com.alutarb.apps.analytics.publication;

import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

import com.alutarb.analytics.publication.application.search.PublicationSearcher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicationsQuestionAnswerer {

    private final ChatClient client;
    private final PublicationSearcher searcher;

    public ChatResponse ask(String question) {

        var results = searcher.searchByQuery(question, 10);

        var context = results.stream()
            .map(r -> r.text())
            .collect(Collectors.joining("\n\n"));

        String prompt = """
            You are an expert data analyst specialized in social-media publications.
            Your task is to answer the user's question strictly based on the information provided
            in the CONTEXT section.

            Semantic and behavioral rules:
            - Do NOT invent information.
            - Do NOT rely on assumptions or external knowledge.
            - Use ONLY the data found explicitly in the context.
            - If the context is insufficient, respond exactly: `No tengo suficiente contexto para responder`.
            - Your analysis must always be clear, concise, and professional.
            - You MUST always answer in Spanish, regardless of the question's language.

            When sufficient context is available:
            - Identify relevant patterns, insights, or relationships present in the data.
            - Provide explanations grounded solely in the given information.
            - Maintain a tone consistent with a senior data analyst.

            CONTEXT:
            %s

            QUESTION:
            %s
            """.formatted(context, question);

        var answer = client
            .prompt()
            .user(prompt)
            .options(
                ChatOptions.builder()
                    .temperature(0.1)
                    .build())
            .call()
            .content();

        return new ChatResponse(answer);
    }

    public record ChatResponse(String answer) {
    }

}