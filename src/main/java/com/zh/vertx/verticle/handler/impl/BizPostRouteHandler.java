package com.zh.vertx.verticle.handler.impl;

import com.zh.vertx.verticle.context.ChainContext;
import com.zh.vertx.verticle.handler.RouteHandler;
import io.vertx.core.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class BizPostRouteHandler implements RouteHandler {
    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String getPath() {
        return "/vertx-demo/:biz/:version/**";
    }

    @Override
    public void handle(ChainContext context) {

    }
}
