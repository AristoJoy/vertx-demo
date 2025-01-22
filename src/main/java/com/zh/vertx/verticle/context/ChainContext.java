package com.zh.vertx.verticle.context;

import com.zh.vertx.verticle.model.BizRequest;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChainContext {

    private RoutingContext routingContext;

    private BizRequest bizRequest;


    public static ChainContext of(RoutingContext routingContext) {
        return ChainContext.builder().routingContext(routingContext).bizRequest(buildBizRequest(routingContext)).build();
    }

    private static BizRequest buildBizRequest(RoutingContext routingContext) {
        String biz = routingContext.pathParam("biz");
        String version = routingContext.pathParam("version");
        String uri = routingContext.request().uri();
        String path = routingContext.request().path();
        String queryString = routingContext.request().query();
        MultiMap headers = routingContext.request().headers();
        String requestId = headers.get("x-request-id");

        return BizRequest.builder().biz(biz).requestId(requestId).version(version).uri(uri).body(routingContext.getBodyAsString()).path(path).queryString(queryString).method(routingContext.request().method().toString()).build();
    }


}
