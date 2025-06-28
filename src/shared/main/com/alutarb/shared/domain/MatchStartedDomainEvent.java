package com.alutarb.shared.domain;

import com.alutarb.shared.domain.bus.event.DomainEvent;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class MatchStartedDomainEvent extends DomainEvent{

    private final String matchId;
    private final String teamHome;
    private final String teamAway;
    private final ZonedDateTime startDate;
    private final String status;

    public MatchStartedDomainEvent(String matchId, String teamHome, String teamAway, ZonedDateTime startDate, String status) {
        super(matchId);
        this.matchId = matchId;
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.startDate = startDate;
        this.status = status;
    }

    @Override
    public String eventName() {
        return "match.started";
    }

    @Override
    public HashMap<String, Serializable> toPrimitives() {
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("matchId", matchId);
        map.put("teamHome", teamHome);
        map.put("teamAway", teamAway);
        map.put("startDate", startDate != null ? startDate.toString() : null);
        map.put("status", status);
        return map;
    }

    @Override
    public DomainEvent fromPrimitives(String aggregateId, HashMap<String, Serializable> body,
                                      String eventId, String occurredOn) {
        return new MatchStartedDomainEvent(
                aggregateId,
                (String) body.get("teamHome"),
                (String) body.get("teamAway"),
                body.get("startDate") != null ? ZonedDateTime.parse((String) body.get("startDate")) : null,
                (String) body.get("status")
        );
    }

    public String matchId() { return matchId; }
    public String teamHome() { return teamHome; }
    public String teamAway() { return teamAway; }
    public ZonedDateTime startDate() { return startDate; }
    public String status() { return status; }
}
