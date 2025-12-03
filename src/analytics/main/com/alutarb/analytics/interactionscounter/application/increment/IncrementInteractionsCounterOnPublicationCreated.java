package com.alutarb.analytics.interactionscounter.application.increment;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.alutarb.shared.domain.Constants;
import com.alutarb.shared.domain.PublicationCreatedDomainEvent;
import com.alutarb.shared.domain.bus.event.DomainEventSubscriber;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@DomainEventSubscriber(PublicationCreatedDomainEvent.class)
public class IncrementInteractionsCounterOnPublicationCreated {

    private final InteractionsCounterIncrementer incrementer;

    @EventListener
    public void on(PublicationCreatedDomainEvent event) {
        int likes = event.likes() != null ? event.likes() : 0;
        int shares = event.shares() != null ? event.shares() : 0;
        int views = event.views() != null ? event.views() : 0;
        int comments = event.comments() != null ? event.comments() : 0;

        incrementer.increment(Constants.ID, event.aggregateId(), likes, shares, views, comments);
    }

}
