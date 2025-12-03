package com.alutarb.apps.shared;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.analytics.publication.application.create.CreatePublicationCommand;
import com.alutarb.analytics.publication.application.create.PublicationCreator;
import com.alutarb.analytics.shared.infrastructure.RawPublicationSearcher;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PublicationSyncronizerController {

    private final RawPublicationSearcher rawSearcher;
    private final PublicationCreator publicationCreator;

    @PostMapping("/start-sync")
    public ResponseEntity<Void> sync(@RequestParam int total, @RequestParam(defaultValue = "20") int batchSize) {
        syncAsync(total, batchSize);
        return ResponseEntity.ok().build();
    }

    @Async
    public void syncAsync(int total, int batchSize) {
        int processed = 0;
        System.out.println("::: iniciando sync de " + total + " publicaciones :::");

        while (processed < total) {

            var records = rawSearcher.search(processed, batchSize);

            if (records.isEmpty()) {
                processed += batchSize;
                System.out.println("::: avanzando a " + processed + "/" + total + " ::: ");
                continue;
            }

            var commands = records.stream()
                .map(r -> new CreatePublicationCommand(
                    r.id(),
                    r.text(),
                    r.likes(),
                    r.shares(),
                    r.views(),
                    r.comments(),
                    r.sentiment(),
                    r.hashtags(),
                    r.mentions(),
                    r.emojis(),
                    r.concepts(),
                    r.verbs(),
                    r.createdAt()
                )).toList();

            publicationCreator.create(commands);

            processed += records.size();

            System.out.println("::: progreso: " + processed + "/" + total + " ::: ");
        }

        System.out.println("::: sync completado: " + processed + " publicaciones procesadas. ::: ");
    }

}
