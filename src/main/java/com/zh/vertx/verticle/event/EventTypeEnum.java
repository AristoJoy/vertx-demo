package com.zh.vertx.verticle.event;

public enum EventTypeEnum {
    CLOSE_EVENT("closeEvent", "连接关闭事件"),
    EXCEPTION_EVENT("exceptionEvent", "连接异常事件"),
    SHUTDOWN_EVENT("shutdownEvent", "服务器关闭事件");

    private String eventType;
    private String desc;

    EventTypeEnum(String eventType, String desc) {
        this.eventType = eventType;
        this.desc = desc;
    }
}
