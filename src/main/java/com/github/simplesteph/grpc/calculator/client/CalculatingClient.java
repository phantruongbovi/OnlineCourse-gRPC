package com.github.simplesteph.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatingClient {
    public static void main(String[] args) throws SSLException {
        CalculatingClient main = new CalculatingClient();
        main.run();

    }

    public void run () throws SSLException {
        // with out SSL
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        // With SSL
        // With server authentication SSL/TLS; custom CA root certificates; not on Android
        ManagedChannel securityChannel = NettyChannelBuilder.forAddress("localhost", 50051)
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
                .build();

        //doUnaryCalculate(channel);
        //doStreamingServer(channel);
        //doStreamingClient(channel);
        //doBiDirection(channel);
        //doSquareError(channel);
        //doUnaryTradeWithDeadline(channel);
        doUnaryCalculate(securityChannel);
        channel.shutdown();
    }

    private void doUnaryCalculate(ManagedChannel channel){
        CalculateServiceGrpc.CalculateServiceBlockingStub stub = CalculateServiceGrpc.newBlockingStub(channel);

        SumCalculateResquest resquest = SumCalculateResquest.newBuilder()
                .setNum1(5)
                .setNum2(6)
                .build();

        SumCalculateResponse result = stub.sumCalculate(resquest);
        System.out.println(result.getResult());
    }

    private void doStreamingServer(ManagedChannel channel){
        CalculateServiceGrpc.CalculateServiceBlockingStub stub = CalculateServiceGrpc.newBlockingStub(channel);
        DeviceCalculateRequest request = DeviceCalculateRequest.newBuilder().setNum(120).build();
        stub.deviceCalculate(request).forEachRemaining(deviceCalculateResponse -> {
            System.out.println(deviceCalculateResponse.getResult());
        });
    }

    private void doStreamingClient(ManagedChannel channel){
        CalculateServiceGrpc.CalculateServiceStub stub = CalculateServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<MedianCalculateRequest> requestStreamObserver = stub.medianCalculate(new StreamObserver<MedianCalculateResponse>() {
            @Override
            public void onNext(MedianCalculateResponse value) {
                System.out.println("Result From the server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Completed claim Response!");
                latch.countDown();
            }
        }) ;
        requestStreamObserver.onNext(
                MedianCalculateRequest.newBuilder().setNum(5).build()
        );
        requestStreamObserver.onNext(
                MedianCalculateRequest.newBuilder().setNum(6).build()
        );
        requestStreamObserver.onNext(
                MedianCalculateRequest.newBuilder().setNum(7).build()
        );
        requestStreamObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDirection(ManagedChannel channel){
        CalculateServiceGrpc.CalculateServiceStub stub = CalculateServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<FindMaxRequest> requestStreamObserver = stub.findMaxCalculate(new StreamObserver<FindMaxRespone>() {
            int current = 0;
            @Override
            public void onNext(FindMaxRespone value) {
                if (current != value.getResult()) {
                    System.out.println("Max number now is: " + value.getResult());
                    current = value.getResult();
                }
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Has completed!");
                latch.countDown();
            }
        });
        Arrays.asList(1, 5, 3, 6, 2, 20).forEach( integer -> {
                System.out.println("Num " + integer + " sent!");
            try {
                requestStreamObserver.onNext(
                        FindMaxRequest.newBuilder().setNum(integer).build()
                );
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            }
        );
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void doSquareError(ManagedChannel channel){
        CalculateServiceGrpc.CalculateServiceBlockingStub stub = CalculateServiceGrpc.newBlockingStub(channel);
        try{
          SquareErrorResponse response = stub.squareError(
                  SquareErrorRequest.newBuilder().setNum(-5).build()
          );
          System.out.println("The result is: " + response.getResult());
        } catch (StatusRuntimeException e) {
            System.out.print("The error is : ");
            e.printStackTrace();
        }
    }

    private void doUnaryTradeWithDeadline(ManagedChannel channel){
        CalculateServiceGrpc.CalculateServiceBlockingStub stub = CalculateServiceGrpc.newBlockingStub(channel);

        // first call 3000ms deadline
        try {
            TradeWithDeadlineResponse response  = stub.withDeadlineAfter(3000, TimeUnit.MILLISECONDS)
                    .tradeWithDeadline(TradeWithDeadlineRequest.newBuilder().setNum(10).build());
            System.out.println("The time in trading is 3000ms");
            System.out.println("The item has trade is: " + response.getResult());
        }catch (StatusRuntimeException e){
            if(e.getStatus() == Status.DEADLINE_EXCEEDED){
                System.out.println("Deadline has been exceeded, we don't want the response after 3000ms");
            }
            else{
                e.printStackTrace();
            }
        }

        // first call 50ms deadline
        try {
            TradeWithDeadlineResponse response  = stub.withDeadline(Deadline.after(50, TimeUnit.MILLISECONDS))
                    .tradeWithDeadline(TradeWithDeadlineRequest.newBuilder().setNum(10).build());
            System.out.println("The item has trade is: " + response.getResult());
        }catch (StatusRuntimeException e){
            if(e.getStatus() == Status.DEADLINE_EXCEEDED){
                System.out.println("Deadline has been exceeded, we don't want the response after 50ms");
            }
            else{
                e.printStackTrace();
            }
        }

    }
}

