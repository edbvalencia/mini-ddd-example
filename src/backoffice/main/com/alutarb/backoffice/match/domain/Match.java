package com.alutarb.backoffice.match.domain;

import com.alutarb.shared.domain.AggregateRoot;
import com.alutarb.shared.domain.MatchCreatedDomainEvent;
import com.alutarb.shared.domain.MatchFinishedDomainEvent;
import com.alutarb.shared.domain.MatchStartedDomainEvent;

import java.time.ZonedDateTime;

public class Match extends AggregateRoot {

    private final String id;
    private final String teamHome;
    private final String teamAway;
    private final ZonedDateTime startDate;
    private MatchStatus status;

    public Match(String id, String teamHome, String teamAway, ZonedDateTime startDate, MatchStatus status) {
        this.id = id;
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.startDate = startDate;
        this.status = status;
    }

    public static Match create(String id, String teamHome, String teamAway) {
        Match match = new Match(id, teamHome, teamAway, ZonedDateTime.now(), MatchStatus.SCHEDULED);
        match.record(new MatchCreatedDomainEvent(match.id(), match.teamHome(), match.teamAway(), match.startDate()));
        return match;
    }

    public void start() {
        this.status = MatchStatus.IN_PROGRESS;
        this.record(new MatchStartedDomainEvent(id, teamHome, teamAway, startDate, status.name()));
    }

    public void finish() {
        this.status = MatchStatus.FINISHED;
        this.record(new MatchFinishedDomainEvent(id, teamHome, teamAway, startDate, status.name()));
    }

    public String id() {
        return id;
    }

    public String teamHome() {
        return teamHome;
    }

    public String teamAway() {
        return teamAway;
    }

    public ZonedDateTime startDate() {
        return startDate;
    }

    public MatchStatus status() {
        return status;
    }

    public enum MatchStatus {
        SCHEDULED,
        IN_PROGRESS,
        FINISHED
    }

}
