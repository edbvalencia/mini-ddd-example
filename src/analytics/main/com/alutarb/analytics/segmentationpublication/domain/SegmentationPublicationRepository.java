package com.alutarb.analytics.segmentationpublication.domain;

import java.time.Instant;
import java.util.List;

public interface SegmentationPublicationRepository {

    List<SegmentationPublication> searchByQuery(String query, int limit);

    List<SegmentationPublication> searchByQuery(String query, int limit, Instant createdAtFrom, Instant createdAtTo);

    SegmentationPublicationSearchResult searchByQueryWithStats(String query, int limit);

    SegmentationPublicationSearchResult searchByQueryWithStats(String query, int limit, Instant createdAtFrom,
        Instant createdAtTo);

    void save(SegmentationPublication publication);

}
