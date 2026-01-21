package com.alutarb.analytics.segmentationpublication.application.create;

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
            command.comments(),
            command.interactions(),
            command.reactions(),
            command.shares(),
            command.socialNetwork(),
            command.text(),
            command.createdAt(),
            command.embedding(),
            command.color(),
            command.emotion(),
            command.link(),
            command.media(),
            command.gobColor(),
            command.itemType(),
            command.page(),
            command.avatar(),
            command.dataType(),
            command.impactLevel(),
            command.platform(),
            command.reachLevel(),
            command.registeredAt(),
            command.bigFive(),
            command.cleanText(),
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

}
