package com.alutarb.analytics.segmentationpublication.domain;

import java.util.List;

public interface SegmentationPublicationRepository {

    List<SegmentationPublication> searchByQuery(String query, int limit);

    void save(SegmentationPublication publication);

}
