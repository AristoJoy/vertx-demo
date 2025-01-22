package com.zh.vertx.verticle.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionEvent {
    private EventTypeEnum eventType;

    private String remoteAddress;

    private Throwable throwable;
}
