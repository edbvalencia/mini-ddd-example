package com.alutarb.analytics.publicationscounter.domain;

import java.util.ArrayList;
import java.util.List;

public class PublicationsCounter {

    private String id;
    private List<String> publicationsIds;
    private int count;

    public PublicationsCounter(String id, List<String> publicationsIds, int count) {
        this.id = id;
        this.publicationsIds = publicationsIds;
        this.count = count;
    }

    public static PublicationsCounter initialize(String id) {
        return new PublicationsCounter(id, new ArrayList<>(), 0);
    }

    public void increment(String publicationId) {
        this.publicationsIds.add(publicationId);
        this.count++;
    }

    public boolean alreadyCounted(String publicationId) {
        return this.publicationsIds.contains(publicationId);
    }

    public int count() {
        return count;
    }

}
