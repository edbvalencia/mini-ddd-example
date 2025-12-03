package com.alutarb.analytics.interactionscounter.domain;

import java.util.ArrayList;
import java.util.List;

public class InteractionsCounter {

    private String id;
    private List<String> publicationsIds;
    private int likesCount;
    private int sharesCount;
    private int viewsCount;
    private int commentsCount;

    public InteractionsCounter(String id, List<String> publicationsIds, int likesCount, int sharesCount, int viewsCount,
        int commentsCount) {
        this.id = id;
        this.publicationsIds = publicationsIds;
        this.likesCount = likesCount;
        this.sharesCount = sharesCount;
        this.viewsCount = viewsCount;
        this.commentsCount = commentsCount;
    }

    public static InteractionsCounter initialize(String id) {
        return new InteractionsCounter(id, new ArrayList<>(), 0, 0, 0, 0);
    }

    public void increment(String publicationId, int likesCount, int sharesCount, int viewsCount, int commentsCount) {
        this.publicationsIds.add(publicationId);
        this.likesCount += likesCount;
        this.sharesCount += sharesCount;
        this.viewsCount += viewsCount;
        this.commentsCount += commentsCount;
    }

    public boolean alreadyCounted(String publicationId) {
        return this.publicationsIds.contains(publicationId);
    }

    public int likesCount() {
        return likesCount;
    }

    public int sharesCount() {
        return sharesCount;
    }

    public int viewsCount() {
        return viewsCount;
    }

    public int commentsCount() {
        return commentsCount;
    }

}
