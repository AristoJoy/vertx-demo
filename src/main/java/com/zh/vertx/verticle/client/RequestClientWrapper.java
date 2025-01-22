package com.zh.vertx.verticle.client;

import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.client.WebClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestClientWrapper {
    private HttpClient client;

    private WebClient webClient;
}
