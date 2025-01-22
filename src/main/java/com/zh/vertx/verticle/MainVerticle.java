package com.zh.vertx.verticle;

import com.zh.vertx.verticle.client.RequestClientWrapper;
import com.zh.vertx.verticle.context.ChainContext;
import com.zh.vertx.verticle.event.ConnectionEvent;
import com.zh.vertx.verticle.event.EventConstant;
import com.zh.vertx.verticle.event.EventTypeEnum;
import com.zh.vertx.verticle.handler.RouteHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class MainVerticle extends AbstractVerticle {

    private RequestClientWrapper client;

    @Resource
    private List<RouteHandler> handlers;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        createClient();
        configureHandlers(router);
        startHttpServer(router, startPromise);
    }

    private void createClient() {
        HttpClientOptions options = new HttpClientOptions().setProtocolVersion(HttpVersion.HTTP_2);
        HttpClient httpClient = vertx.createHttpClient(options).connectionHandler(this::connectionHandler);
        WebClientOptions webClientOptions = new WebClientOptions().setProtocolVersion(HttpVersion.HTTP_2);
        WebClient webClient = WebClient.wrap(httpClient, webClientOptions);
        client = RequestClientWrapper.builder().client(httpClient).webClient(webClient).build();
    }

    private void configureHandlers(Router router) {
        router.route().handler(BodyHandler.create());
        for (RouteHandler handler : handlers) {
            router.route(handler.getMethod(), handler.getPath()).handler(ctx -> {
                ChainContext chainContext = ChainContext.of(ctx);
                handler.handle(chainContext);
            });
        }
    }

    private void startHttpServer(Router router, Promise<Void> startPromise) {
        vertx.createHttpServer().requestHandler(router).listen(8080, "localhost", res -> {
            if (res.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(res.cause());
            }
        });
    }

    private void connectionHandler(HttpConnection httpConnection) {
        httpConnection.closeHandler(event -> {
            String remoteAddr = httpConnection.remoteAddress().toString();
            ConnectionEvent connectionEvent = ConnectionEvent.builder().eventType(EventTypeEnum.CLOSE_EVENT).remoteAddress(remoteAddr).build();
            vertx.eventBus().publish(EventConstant.CONNECTION_EVENT_BUS_ADDR, connectionEvent);
            log.info("Connection close event, remoteAddr: {}", remoteAddr);
        });
        httpConnection.exceptionHandler(event -> {
            String remoteAddr = httpConnection.remoteAddress().toString();
            ConnectionEvent connectionEvent = ConnectionEvent.builder().eventType(EventTypeEnum.EXCEPTION_EVENT).remoteAddress(remoteAddr).throwable(event).build();
            vertx.eventBus().publish(EventConstant.CONNECTION_EVENT_BUS_ADDR, connectionEvent);
            log.info("Connection exception event, remoteAddr: {}", remoteAddr);
        });
        httpConnection.shutdown(event -> {
            String remoteAddr = httpConnection.remoteAddress().toString();
            ConnectionEvent connectionEvent = ConnectionEvent.builder().eventType(EventTypeEnum.CLOSE_EVENT).remoteAddress(remoteAddr).build();
            vertx.eventBus().publish(EventConstant.CONNECTION_EVENT_BUS_ADDR, connectionEvent);
            log.info("Connection shutdown event, remoteAddr: {}", remoteAddr);
        });
    }
}
