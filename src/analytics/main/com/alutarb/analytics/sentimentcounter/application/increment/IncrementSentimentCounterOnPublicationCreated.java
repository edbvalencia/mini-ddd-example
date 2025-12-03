package com.alutarb.analytics.sentimentcounter.application.increment;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.alutarb.shared.domain.Constants;
import com.alutarb.shared.domain.PublicationCreatedDomainEvent;
import com.alutarb.shared.domain.bus.event.DomainEventSubscriber;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@DomainEventSubscriber(PublicationCreatedDomainEvent.class)
public class IncrementSentimentCounterOnPublicationCreated {

    private final SentimentCounterIncrementer incrementer;

    @EventListener
    public void on(PublicationCreatedDomainEvent event) {
        var positiveCount = event.sentiment().isPositive() ? 1 : 0;
        var negativeCount = event.sentiment().isNegative() ? 1 : 0;
        var neutralCount = event.sentiment().isNeutral() ? 1 : 0;
        incrementer.increment(Constants.ID, event.aggregateId(), positiveCount, negativeCount, neutralCount);
    }

}
