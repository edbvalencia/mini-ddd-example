package com.alutarb.apps.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ServerSideEventBus {

    private final Sinks.Many<ServerSentEvent<Object>> sink = Sinks.many().multicast().directBestEffort();

    public Flux<ServerSentEvent<Object>> subscribe() {
        return sink.asFlux()
                .timeout(Duration.ofHours(24));
    }

    public void publish(Object event) {
        sink.tryEmitNext(ServerSentEvent.builder(event).build());
    }

}
