package com.github.simplesteph.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello, this is server Calculator");
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatingServiceImpl())
                .build();

        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("Request shutdown server!");
            server.shutdown();
            System.out.println("Successfully stopped server");
        }));


        server.awaitTermination();
    }
}
