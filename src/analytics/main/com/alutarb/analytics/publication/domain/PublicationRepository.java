package com.alutarb.analytics.publication.domain;

import java.util.List;

public interface PublicationRepository {

    List<Publication> searchByQuery(String query, int limit);

    void save(Publication publication);

    void save(List<Publication> publications);

    void deleteAll();

}
