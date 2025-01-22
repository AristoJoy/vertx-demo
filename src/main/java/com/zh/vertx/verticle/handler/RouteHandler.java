package com.zh.vertx.verticle.handler;

import com.zh.vertx.verticle.context.ChainContext;
import io.vertx.core.http.HttpMethod;

public interface RouteHandler {
    HttpMethod getMethod();
    String getPath();

    void handle(ChainContext context);
}
