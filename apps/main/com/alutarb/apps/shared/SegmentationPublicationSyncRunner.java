package com.alutarb.apps.shared;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alutarb.analytics.segmentationpublication.application.create.CreateSegmentationPublicationCommand;
import com.alutarb.analytics.segmentationpublication.application.create.SegmentationPublicationCreator;
import com.alutarb.analytics.shared.domain.RawMention;
import com.alutarb.analytics.shared.infrastructure.RawSegmentationPublicationSearcher;
import com.alutarb.shared.domain.SocialNetwork;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SegmentationPublicationSyncRunner {

    private final RawSegmentationPublicationSearcher rawSearcher;
    private final SegmentationPublicationCreator segmentationPublicationCreator;

    @Async("segmentationExecutor")
    public CompletableFuture<Void> runAsync(int total, int batchSize) {

        long totalStart = System.currentTimeMillis();

        int offset = 0;
        int processed = 0;

        long totalSearchTime = 0;
        long totalSaveTime = 0;

        long checkpointStart = System.currentTimeMillis();
        int checkpointSize = 1000;
        int lastCheckpoint = 0;

        while (true) {

            long searchStart = System.currentTimeMillis();
            List<RawMention> records = rawSearcher.search(offset, batchSize);
            long searchEnd = System.currentTimeMillis();
            totalSearchTime += (searchEnd - searchStart);

            if (records.isEmpty()) {
                break;
            }

            var commands = records.stream()
                .map(r -> new CreateSegmentationPublicationCommand(
                    r.id(),
                    r.audience(),
                    r.comments(),
                    r.interactions(),
                    r.reactions(),
                    r.shares(),
                    SocialNetwork.fromString(r.network()),
                    r.text(),
                    r.createdAt(),
                    null
                ))
                .toList();

            long saveStart = System.currentTimeMillis();
            commands.forEach(segmentationPublicationCreator::create);
            long saveEnd = System.currentTimeMillis();
            totalSaveTime += (saveEnd - saveStart);

            processed += records.size();
            offset += records.size();

            if (processed - lastCheckpoint >= checkpointSize) {
                long now = System.currentTimeMillis();
                System.out.println(
                    "[SYNC] saved=" + processed +
                        " batch(ms)=" + (now - checkpointStart)
                );
                lastCheckpoint = processed;
                checkpointStart = now;
            }

            if (processed >= total) {
                break;
            }
        }

        long totalEnd = System.currentTimeMillis();

        System.out.println("sync finished");
        System.out.println("fetch total(ms): " + totalSearchTime);
        System.out.println("save total(ms): " + totalSaveTime);
        System.out.println("total(ms): " + (totalEnd - totalStart));

        return CompletableFuture.completedFuture(null);
    }

    @Async("segmentationExecutor")
    public CompletableFuture<Void> syncLatestAsync() {
        long totalStart = System.currentTimeMillis();

        List<RawMention> records = rawSearcher.search(0, 10000);

        var commands = records.stream()
            .map(r -> new CreateSegmentationPublicationCommand(
                r.id(),
                r.audience(),
                r.comments(),
                r.interactions(),
                r.reactions(),
                r.shares(),
                SocialNetwork.fromString(r.network()),
                r.text(),
                r.createdAt(),
                null
            ))
            .toList();

        commands.forEach(segmentationPublicationCreator::create);

        long totalEnd = System.currentTimeMillis();
        System.out.println("[SYNC-LATEST] saved=" + records.size() + " total(ms)=" + (totalEnd - totalStart));

        return CompletableFuture.completedFuture(null);
    }

}