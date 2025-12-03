package com.alutarb.analytics.publicationscounter.domain;

public interface PublicationsCounterRepository {

    PublicationsCounter search(String id);

    void save(PublicationsCounter counter);

}
