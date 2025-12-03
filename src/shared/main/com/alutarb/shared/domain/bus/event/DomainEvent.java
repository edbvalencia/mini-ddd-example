package com.alutarb.shared.domain.bus.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public abstract class DomainEvent {

    private String aggregateId;
    private String eventId;
    private String occurredOn;

    public DomainEvent() {
        this.aggregateId = null;
        this.eventId = null;
        this.occurredOn = null;
    }

    public DomainEvent(String aggregateId) {
        this.aggregateId = aggregateId;
        this.eventId = NanoIdUtils.randomNanoId();
        this.occurredOn = Instant.now().toString();
    }

    public DomainEvent(String aggregateId, String eventId, String occurredOn) {
        this.aggregateId = aggregateId;
        this.eventId = eventId;
        this.occurredOn = occurredOn;
    }

    public abstract String eventName();

    public abstract HashMap<String, Serializable> toPrimitives();

    public abstract DomainEvent fromPrimitives(
        String aggregateId,
        HashMap<String, Serializable> body,
        String eventId,
        String occurredOn
    );

    public String aggregateId() {
        return aggregateId;
    }

    public String eventId() {
        return eventId;
    }

    public String occurredOn() {
        return occurredOn;
    }
}
