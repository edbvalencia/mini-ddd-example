package com.alutarb.apps.backoffice.match;

import com.alutarb.apps.notifications.ServerSideEventBus;
import com.alutarb.backoffice.match.application.create.CreateMatchRequest;
import com.alutarb.backoffice.match.application.create.MatchCreator;
import com.alutarb.backoffice.match.application.finish.MatchFinisher;
import com.alutarb.backoffice.match.application.start.MatchStarter;
import com.alutarb.backoffice.match.domain.MatchRepository;
import com.alutarb.shared.domain.MatchFinishedDomainEvent;
import com.alutarb.shared.domain.MatchStartedDomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class MatchController {

    private final MatchCreator creator;
    private final MatchRepository repository;
    private final MatchStarter starter;
    private final MatchFinisher finisher;
    private final ServerSideEventBus bus;

    record MatchResponse(
            String id,
            String teamHome,
            String teamAway,
            ZonedDateTime startDate,
            String status
    ) {
    }

    @GetMapping("/matches")
    public List<MatchResponse> getAllMatches() {
        return repository.searchAll()
                .stream()
                .map(match -> new MatchResponse(
                        match.id(),
                        match.teamHome(),
                        match.teamAway(),
                        match.startDate(),
                        match.status().name()
                ))
                .toList();
    }

    @PostMapping("/matches")
    public ResponseEntity<Void> create(@RequestBody CreateMatchRequest request) {
        creator.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/match-starts/{id}")
    public ResponseEntity<Void> startMatch(@PathVariable("id") String matchId) {
        starter.start(matchId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/match-finishes/{id}")
    public ResponseEntity<Void> finishMatch(@PathVariable("id") String matchId) {
        finisher.finish(matchId);
        return ResponseEntity.noContent().build();
    }

    @EventListener
    public void on(MatchFinishedDomainEvent event) {
        Map<String, String> payload = Map.of(
                "eventName", event.eventName(),
                "matchId", event.matchId(),
                "matchTeams", event.teamHome() + " vs " + event.teamAway()
        );
        bus.publish(payload);
    }

    @EventListener
    public void on(MatchStartedDomainEvent event) {
        Map<String, String> payload = Map.of(
                "eventName", event.eventName(),
                "matchId", event.matchId(),
                "matchTeams", event.teamHome() + " vs " + event.teamAway()
        );
        bus.publish(payload);
    }

    @GetMapping(path = "/matches/stream", produces = "text/event-stream")
    public Flux<ServerSentEvent<Object>> streamMatches() {
        return bus.subscribe();
    }

}