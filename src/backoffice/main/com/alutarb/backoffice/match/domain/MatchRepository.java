package com.alutarb.backoffice.match.domain;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {

    void save(Match match);

    List<Match> searchAll();

    Optional<Match> search(String id);

}
