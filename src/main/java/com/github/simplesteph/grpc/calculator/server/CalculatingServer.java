package com.github.simplesteph.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.File;
import java.io.IOException;

public class CalculatingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello, this is server Calculator");
        /*// Normal server
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatingServiceImpl())
                .build();*/

        // Server using SSL
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatingServiceImpl())
                .addService(ProtoReflectionService.newInstance()) //reflection
                .useTransportSecurity(
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem")
                )
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
