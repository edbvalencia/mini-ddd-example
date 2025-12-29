package com.alutarb.apps.shared;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SegmentationPublicationSyncronizerController {

    private final SegmentationPublicationSyncRunner syncRunner;

    @PostMapping("/start-segmentation-sync")
    public ResponseEntity<Void> sync(
        @RequestParam int total,
        @RequestParam(defaultValue = "20") int batchSize
    ) {
        syncRunner.runAsync(total, batchSize);
        return ResponseEntity.accepted().build();
    }

}
