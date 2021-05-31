package com.github.simplesteph.grpc.square.client;

import com.proto.square.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SquareClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        SquareServiceGrpc.SquareServiceBlockingStub stub = SquareServiceGrpc.newBlockingStub(channel);

        /*RectangleRequest rectangleRequest = RectangleRequest.newBuilder()
                .setChieuRong(5)
                .setChieuDai(10)
                .build();

        RectangleResponse rectangleResponse = stub.rectangle(rectangleRequest);
        System.out.println("Dien tich la: " + rectangleResponse);*/

        RectangleManyTimesRequest request = RectangleManyTimesRequest.newBuilder()
        .setNum1(5)
        .setNum2(6)
        .build();

        stub.rectangleManyTimes(request)
                .forEachRemaining(rectangleManyTimesResponse -> {
                    System.out.println("Dien tien la: " + rectangleManyTimesResponse.getResult());
                });
        channel.shutdown();

    }
}
