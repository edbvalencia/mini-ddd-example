package com.alutarb.backoffice.match.application.finish;

import com.alutarb.backoffice.match.domain.Match;
import com.alutarb.backoffice.match.domain.MatchRepository;
import com.alutarb.shared.domain.bus.event.EventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchFinisher {

    private final EventBus eventBus;
    private final MatchRepository repository;

    public void finish(String matchId) {
        Match match = repository.search(matchId).orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        match.finish();

        repository.save(match);
        
        eventBus.publish(match.pullDomainEvents());
    }

}