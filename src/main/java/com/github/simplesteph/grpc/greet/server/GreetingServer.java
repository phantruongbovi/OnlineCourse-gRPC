package com.github.simplesteph.grpc.greet.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello server");
        // Create server at port 50051
        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetingServiceImpl())
                .build();

        // Start server
        server.start();

        // Terminate server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server!");
        }));

        // Wait server run
        server.awaitTermination();
    }
}
