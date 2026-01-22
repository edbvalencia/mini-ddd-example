package com.alutarb.apps.shared;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alutarb.analytics.shared.domain.RawMention;
import com.alutarb.analytics.shared.infrastructure.RawMentionQdrantRepository;
import com.alutarb.analytics.shared.infrastructure.RawSegmentationPublicationSearcher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FixedDateRangeSyncRunner {

    private static final int BATCH_SIZE = 500;

    private final RawSegmentationPublicationSearcher rawSearcher;
    private final RawMentionQdrantRepository qdrantRepository;

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

            records.forEach(qdrantRepository::save);

            if (!records.isEmpty()) {
                var firstRecord = records.get(0);
                int textLength = firstRecord.text() != null ? firstRecord.text().length() : 0;
                System.out.println("[FIXED-SYNC] Sample text length: " + textLength + " chars");
                System.out.println("[FIXED-SYNC] Sample text preview: " +
                    (firstRecord.text() != null && firstRecord.text().length() > 100 ? firstRecord.text().substring(0,
                        100) + "..." : firstRecord.text()));
            }

            System.out.println("[FIXED-SYNC] Saved " + records.size() + " records to Qdrant (duplicates overwritten)");

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

}
