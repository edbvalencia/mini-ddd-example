package com.alutarb.backoffice.match.application.create;

import com.alutarb.backoffice.match.domain.Match;
import com.alutarb.backoffice.match.domain.MatchRepository;
import com.alutarb.shared.domain.bus.event.EventBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchCreator {

    private final MatchRepository repository;
    private final EventBus eventBus;

    public void create(CreateMatchRequest request) {
        Match match = Match.create(
                request.id(),
                request.teamHome(),
                request.teamAway()
        );

        repository.save(match);

        eventBus.publish(match.pullDomainEvents());
    }

}