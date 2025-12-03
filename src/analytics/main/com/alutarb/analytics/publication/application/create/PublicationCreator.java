package com.alutarb.analytics.publication.application.create;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.publication.domain.Publication;
import com.alutarb.analytics.publication.domain.PublicationRepository;
import com.alutarb.shared.domain.bus.event.EventBus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicationCreator {

    private final EventBus eventBus;
    private final PublicationRepository repository;

    public void create(CreatePublicationCommand command) {
        var publication = Publication.create(
            command.id(),
            command.text(),
            command.hashtags(),
            command.mentions(),
            command.emojis(),
            command.concepts(),
            command.verbs(),
            command.likes(),
            command.shares(),
            command.views(),
            command.comments(),
            command.sentiment(),
            command.createdAt()
        );
        repository.save(publication);
        eventBus.publish(publication.pullDomainEvents());
    }

    public void create(List<CreatePublicationCommand> commands) {
        var publications = commands.stream().map(command -> Publication.create(
            command.id(),
            command.text(),
            command.hashtags(),
            command.mentions(),
            command.emojis(),
            command.concepts(),
            command.verbs(),
            command.likes(),
            command.shares(),
            command.views(),
            command.comments(),
            command.sentiment(),
            command.createdAt()
        )).toList();

        repository.save(publications);
        eventBus.publish(publications.stream().flatMap(p -> p.pullDomainEvents().stream()).toList());
    }

}
