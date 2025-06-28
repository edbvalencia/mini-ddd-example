package com.alutarb.shared.domain.bus.event;

import java.io.Serializable;
import java.util.HashMap;

public class UnknownDomainEvent extends DomainEvent {

    private String originalType;
    private String rawData;

    public UnknownDomainEvent() {
        super();
        this.originalType = null;
        this.rawData = null;
    }

    public UnknownDomainEvent(String aggregateId, String eventId, String occurredOn, String originalType, String rawData) {
        super(aggregateId, eventId, occurredOn);
        this.originalType = originalType;
        this.rawData = rawData;
    }

    public UnknownDomainEvent(String aggregateId, String originalType, String rawData) {
        super(aggregateId);
        this.originalType = originalType;
        this.rawData = rawData;
    }

    @Override
    public String eventName() {
        return "unknown";
    }

    @Override
    public HashMap<String, Serializable> toPrimitives() {
        HashMap<String, Serializable> data = new HashMap<>();
        data.put("originalType", originalType);
        data.put("rawData", rawData);
        return data;
    }

    @Override
    public DomainEvent fromPrimitives(String aggregateId, HashMap<String, Serializable> body, String eventId, String occurredOn) {
        return new UnknownDomainEvent(
                aggregateId,
                eventId,
                occurredOn,
                (String) body.get("originalType"),
                (String) body.get("rawData")
        );
    }

    public String originalType() {
        return originalType;
    }

    public String rawData() {
        return rawData;
    }

}