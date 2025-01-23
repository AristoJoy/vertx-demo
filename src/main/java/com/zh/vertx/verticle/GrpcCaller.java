package com.zh.vertx.verticle;

import com.zh.vertx.verticle.proto.GreeterGrpc;
import com.zh.vertx.verticle.proto.HelloReply;
import com.zh.vertx.verticle.proto.HelloRequest;
import io.grpc.MethodDescriptor;
import io.vertx.core.Vertx;
import io.vertx.core.net.SocketAddress;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.common.GrpcReadStream;
import org.springframework.stereotype.Component;

@Component
public class GrpcCaller {
    public void grpcCall(Vertx vertx) {
        GrpcClient client = GrpcClient.client(vertx);
        MethodDescriptor<HelloRequest, HelloReply> methodDescriptor = GreeterGrpc.getSayHelloMethod();
        SocketAddress server = SocketAddress.inetSocketAddress(9090, "localhost");
        client
                .request(server, methodDescriptor).compose(request -> {
                    request.end(HelloRequest
                            .newBuilder()
                            .setBiz("callerTest")
                            .setBody("callerTestBody")
                            .setVersion("1.0")
                            .build());
                    return request.response().compose(GrpcReadStream::last);
                }).onSuccess(reply -> {
                    System.out.println("Received " + reply.getMessage());
                });
    }
}
