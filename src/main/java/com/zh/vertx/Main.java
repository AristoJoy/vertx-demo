package com.zh.vertx;

import com.zh.vertx.verticle.MainVerticle;
import com.zh.vertx.verticle.factory.SpringVerticleFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.core.spi.VerticleFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@Configuration
@ComponentScan("com.zh.vertx")
@Slf4j
public class Main {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Vertx vertx = Vertx.vertx();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);
        vertx.registerVerticleFactory(verticleFactory);

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setWorker(true).setInstances(CpuCoreSensor.availableProcessors());
        vertx.deployVerticle(verticleFactory.prefix() + ":" + MainVerticle.class.getName(), deploymentOptions, result -> {
            if (result.succeeded()) {
                stopWatch.stop();
                log.info("Verticle deployed in {} ms", stopWatch.getTotalTimeMillis());
            } else {
                log.error("Failed to deploy verticle", result.cause());
            }
        });

//        EventBus eventBus = vertx.eventBus();
//
//        eventBus.consumer("vertx.main", message -> {
//            log.info("Received event: {}", message.body());
//        }).completionHandler(result -> {
//            if (result.succeeded()) {
//                log.info("handler register successfully");
//            } else {
//                log.error("handler register failed", result.cause());
//            }
//        });
//
//        eventBus.publish("vertx.main", "Test publish");
//        eventBus.send("vertx.main", "Test publish");

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
            log.info("Received request: {}", request.body().toString());
//            log.info("request headers: {}", request.headers());
            log.info("request version: {}", request.version());
            log.info("request method: {}", request.method());
            log.info("request uri: {}", request.uri());
            log.info("request absoluteURI: {}", request.absoluteURI());
            log.info("request path: {}", request.path());
            log.info("request query: {}", request.query());


            Buffer totalBuffer = Buffer.buffer();

            request.handler(buffer -> {
                System.out.println("I have received a chunk of the body of length " + buffer.length());
                totalBuffer.appendBuffer(buffer);
            });

            request.endHandler(v -> {
                System.out.println("Full body received, length = " + totalBuffer.length());
            });

            Cookie othercookie = request.getCookie("othercookie");
            if (othercookie != null) {
                log.info("Received cookie with name {} and value {}", othercookie.getName(), othercookie.getValue());
            }

            request.response().addCookie(Cookie.cookie("othercookie", "somevalue")).end("Hello World");

        });
        server.listen(8080, "localhost");


    }
}