package com.alutarb.apps.shared;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alutarb.analytics.segmentationpublication.application.CreateSegmentationPublicationCommand;
import com.alutarb.analytics.segmentationpublication.application.SegmentationPublicationCreator;
import com.alutarb.analytics.shared.domain.RawMention;
import com.alutarb.analytics.shared.infrastructure.RawSegmentationPublicationSearcher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SegmentationPublicationSyncRunner {

    private final RawSegmentationPublicationSearcher rawSearcher;
    private final SegmentationPublicationCreator segmentationPublicationCreator;

    @Async("segmentationExecutor")
    public void runAsync(int total, int batchSize) {

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
                    r.avatar(),
                    r.comments(),
                    r.createdAt(),
                    r.dataType(),
                    r.impactLevel(),
                    r.interactions(),
                    r.itemType(),
                    r.link(),
                    r.media(),
                    r.network(),
                    r.page(),
                    r.platform(),
                    r.reachLevel(),
                    r.reactions(),
                    r.registeredAt(),
                    r.shares(),
                    r.text(),
                    r.bigFive(),
                    r.cleanText(),
                    r.color(),
                    r.emotion(),
                    r.gobColor(),
                    r.isValid(),
                    r.municipality(),
                    r.subtopic(),
                    r.summary(),
                    r.title(),
                    r.topic(),
                    r.validText()
                ))
                .toList();

            long saveStart = System.currentTimeMillis();
            segmentationPublicationCreator.create(commands);
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
    }

}