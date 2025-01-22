package com.zh.vertx.verticle.factory;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class SpringVerticleFactory implements VerticleFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public String prefix() {
        return "spring";
    }

    @Override
    public void createVerticle(String name, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
        String clazz = VerticleFactory.removePrefix(name);
        promise.complete(() -> (Verticle) applicationContext.getBean(Class.forName(clazz)));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
