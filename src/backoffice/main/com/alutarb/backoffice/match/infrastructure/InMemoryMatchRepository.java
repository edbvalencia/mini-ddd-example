package com.alutarb.backoffice.match.infrastructure;

import com.alutarb.backoffice.match.domain.Match;
import com.alutarb.backoffice.match.domain.MatchRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMatchRepository implements MatchRepository {

    private final ConcurrentHashMap<String, Match> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Match match) {
        storage.put(match.id(), match);
    }

    @Override
    public List<Match> searchAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Match> search(String id) {
        return Optional.ofNullable(storage.get(id));
    }
    
}