package com.alutarb.analytics.publicationscounter.application.increment;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.alutarb.shared.domain.Constants;
import com.alutarb.shared.domain.PublicationCreatedDomainEvent;
import com.alutarb.shared.domain.bus.event.DomainEventSubscriber;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@DomainEventSubscriber(PublicationCreatedDomainEvent.class)
public class IncrementPublicationsCounterOnPublicationCreated {

    private final PublicationsCounterIncrementer incrementer;

    @EventListener
    public void on(PublicationCreatedDomainEvent event) {
        incrementer.increment(Constants.ID, event.aggregateId());
    }

}
