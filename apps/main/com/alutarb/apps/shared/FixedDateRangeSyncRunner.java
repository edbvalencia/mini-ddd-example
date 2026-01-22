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
public class FixedDateRangeSyncRunner {

    private static final int BATCH_SIZE = 500;

    private final RawSegmentationPublicationSearcher rawSearcher;
    private final SegmentationPublicationCreator segmentationPublicationCreator;

    @Async("segmentationExecutor")
    public CompletableFuture<Void> syncFixedDateRangeAsync() {
        long totalStart = System.currentTimeMillis();

        long totalCount = rawSearcher.countFixedDateRange();
        System.out.println("[FIXED-SYNC] Total records to sync: " + totalCount);

        if (totalCount == 0) {
            System.out.println("[FIXED-SYNC] No records found in date range");
            return CompletableFuture.completedFuture(null);
        }

        int offset = 0;
        int processed = 0;
        int batchNumber = 1;

        while (true) {
            long batchStart = System.currentTimeMillis();

            List<RawMention> records = rawSearcher.searchFixedDateRange(offset, BATCH_SIZE);

            if (records.isEmpty()) {
                break;
            }

            var commands = records.stream()
                .map(this::toCommand)
                .toList();

            commands.forEach(segmentationPublicationCreator::create);

            processed += records.size();
            offset += records.size();

            long batchEnd = System.currentTimeMillis();
            System.out.println("[FIXED-SYNC] Batch " + batchNumber + " completed: " + processed + "/" + totalCount
                + " (" + String.format("%.1f", (processed * 100.0 / totalCount)) + "%) - "
                + (batchEnd - batchStart) + "ms");

            batchNumber++;
        }

        long totalEnd = System.currentTimeMillis();
        System.out.println("[FIXED-SYNC] Sync completed! Total: " + processed + " records in " + (totalEnd - totalStart)
            + "ms");

        return CompletableFuture.completedFuture(null);
    }

    private CreateSegmentationPublicationCommand toCommand(RawMention r) {
        return new CreateSegmentationPublicationCommand(
            r.id(),
            r.audience(),
            r.comments(),
            r.interactions(),
            r.reactions(),
            r.shares(),
            SocialNetwork.fromString(r.network()),
            r.text(),
            r.createdAt(),
            null,
            r.color(),
            r.emotion(),
            r.link(),
            r.media(),
            r.gobColor(),
            r.itemType(),
            r.page(),
            r.avatar(),
            r.dataType(),
            r.impactLevel(),
            r.platform(),
            r.reachLevel(),
            r.registeredAt(),
            r.bigFive(),
            r.cleanText(),
            r.isValid(),
            r.municipality(),
            r.subtopic(),
            r.summary(),
            r.title(),
            r.topic(),
            r.validText()
        );
    }

}
