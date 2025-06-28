package com.alutarb.shared.domain;

import com.alutarb.shared.domain.bus.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {

    @JsonIgnore
    private List<DomainEvent> domainEvents = new ArrayList<>();

    final public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = domainEvents;

        domainEvents = new ArrayList<>();
        
        return events;
    }

    final protected void record(DomainEvent event) {
        domainEvents.add(event);
    }
}
