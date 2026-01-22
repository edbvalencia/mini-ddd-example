package com.alutarb.apps.shared;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alutarb.analytics.shared.infrastructure.RawSegmentationPublicationSearcher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FixedDateRangeSyncInitializer {

    private final FixedDateRangeSyncRunner runner;
    private final RawSegmentationPublicationSearcher rawSearcher;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        long totalCount = rawSearcher.countFixedDateRange();
        System.out.println("========================================");
        System.out.println("[FIXED-SYNC] Application started!");
        System.out.println("[FIXED-SYNC] Date range: 2025-11-01 to 2026-01-24");
        System.out.println("[FIXED-SYNC] Records to sync: " + totalCount);
        System.out.println("[FIXED-SYNC] Starting background sync...");
        System.out.println("========================================");

        runner.syncFixedDateRangeAsync().whenComplete((result, error) -> {
            if (error != null) {
                System.out.println("[FIXED-SYNC] ERROR: " + error.getMessage());
                error.printStackTrace();
            } else {
                System.out.println("[FIXED-SYNC] Background sync completed successfully!");
            }
        });
    }

}
