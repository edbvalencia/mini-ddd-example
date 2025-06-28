package com.alutarb.backoffice.match.application.start;

import com.alutarb.backoffice.match.domain.Match;
import com.alutarb.backoffice.match.domain.MatchRepository;
import com.alutarb.shared.domain.bus.event.EventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchStarter {

    private final MatchRepository repository;
    private final EventBus eventBus;

    public void start(String matchId) {
        Match match = repository.search(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        match.start();

        repository.save(match);
        
        eventBus.publish(match.pullDomainEvents());
    }

}