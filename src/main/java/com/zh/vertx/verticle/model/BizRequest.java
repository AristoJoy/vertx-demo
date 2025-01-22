package com.zh.vertx.verticle.model;

import io.vertx.core.MultiMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizRequest {
    private String requestId;

    private String biz;

    private String version;

    private String method;

    private String uri;

    private String path;

    private String body;

    private String queryString;

    private MultiMap headers;

}
