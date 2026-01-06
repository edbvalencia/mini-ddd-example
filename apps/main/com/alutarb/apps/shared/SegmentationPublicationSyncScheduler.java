package com.alutarb.apps.shared;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SegmentationPublicationSyncScheduler {

    private final SegmentationPublicationSyncRunner runner;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(fixedDelay = 2400000)
    public void syncLatestEvery40Minutes() {
        System.out.println("[SYNC-LATEST] starting sync at " + System.currentTimeMillis());
        if (!running.compareAndSet(false, true)) {
            System.out.println("[SYNC-LATEST] skipped: previous run still in progress");
            return;
        }

        long startTime = System.currentTimeMillis();
        System.out.println("[SYNC-LATEST] starting async task at " + startTime);
        runner.syncLatestAsync().whenComplete((result, error) -> {
            if (error != null) {
                System.out.println("[SYNC-LATEST] error: " + error.getMessage());
            } else {
                System.out.println("[SYNC-LATEST] completed successfully");
            }
            running.set(false);
            long endTime = System.currentTimeMillis();
            System.out.println("[SYNC-LATEST] sync finished at " + endTime + " with status: "
                + (error == null ? "SUCCESS" : "ERROR") + ", duration: " + (endTime - startTime) + "ms");
        });
    }

}
