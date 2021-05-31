package com.github.simplesteph.grpc.square.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class SquareServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello this is a Square Server");
        Server server = ServerBuilder.forPort(50051)
                .addService(new SquareServiceImpl())
                .build();

        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Request Shutdown Server");
            server.shutdown();
            System.out.println("Successfully stopped Server");
        }));
        server.awaitTermination();
    }
}
