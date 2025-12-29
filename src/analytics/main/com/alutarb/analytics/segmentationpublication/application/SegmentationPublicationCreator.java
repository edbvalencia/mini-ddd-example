package com.alutarb.analytics.segmentationpublication.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublication;
import com.alutarb.analytics.segmentationpublication.domain.SegmentationPublicationRepository;
import com.alutarb.shared.domain.bus.event.EventBus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SegmentationPublicationCreator {

    private final EventBus eventBus;
    private final SegmentationPublicationRepository repository;

    public void create(CreateSegmentationPublicationCommand command) {
        var publication = new SegmentationPublication(
            command.id(),
            command.audience(),
            command.avatar(),
            command.comments(),
            command.createdAt(),
            command.dataType(),
            command.impactLevel(),
            command.interactions(),
            command.itemType(),
            command.link(),
            command.media(),
            command.network(),
            command.page(),
            command.platform(),
            command.reachLevel(),
            command.reactions(),
            command.registeredAt(),
            command.shares(),
            command.text(),
            command.bigFive(),
            command.cleanText(),
            command.color(),
            command.emotion(),
            command.gobColor(),
            command.isValid(),
            command.municipality(),
            command.subtopic(),
            command.summary(),
            command.title(),
            command.topic(),
            command.validText()
        );
        repository.save(publication);
        eventBus.publish(publication.pullDomainEvents());
    }

    public void create(List<CreateSegmentationPublicationCommand> commands) {
        var publications = commands.stream().map(command -> new SegmentationPublication(
            command.id(),
            command.audience(),
            command.avatar(),
            command.comments(),
            command.createdAt(),
            command.dataType(),
            command.impactLevel(),
            command.interactions(),
            command.itemType(),
            command.link(),
            command.media(),
            command.network(),
            command.page(),
            command.platform(),
            command.reachLevel(),
            command.reactions(),
            command.registeredAt(),
            command.shares(),
            command.text(),
            command.bigFive(),
            command.cleanText(),
            command.color(),
            command.emotion(),
            command.gobColor(),
            command.isValid(),
            command.municipality(),
            command.subtopic(),
            command.summary(),
            command.title(),
            command.topic(),
            command.validText()
        )).toList();

        repository.save(publications);
        eventBus.publish(publications.stream().flatMap(p -> p.pullDomainEvents().stream()).toList());
    }
}
