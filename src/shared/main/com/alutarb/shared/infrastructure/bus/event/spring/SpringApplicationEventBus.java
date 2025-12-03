package com.alutarb.shared.infrastructure.bus.event.spring;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.alutarb.shared.domain.bus.event.DomainEvent;
import com.alutarb.shared.domain.bus.event.EventBus;

@Primary
@Service
public class SpringApplicationEventBus implements EventBus {

    private final ApplicationEventPublisher publisher;

    public SpringApplicationEventBus(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(final List<DomainEvent> events) {
        events.forEach(this::publish);
    }

    private void publish(final DomainEvent event) {
        this.publisher.publishEvent(event);
    }

}