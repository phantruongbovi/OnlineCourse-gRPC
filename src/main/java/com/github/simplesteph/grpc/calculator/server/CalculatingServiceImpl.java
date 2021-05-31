package com.github.simplesteph.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CalculatingServiceImpl extends CalculateServiceGrpc.CalculateServiceImplBase {
    // UNARY
    @Override
    public void sumCalculate(SumCalculateResquest request, StreamObserver<SumCalculateResponse> responseObserver) {
        int num1 = request.getNum1();
        int num2 = request.getNum2();
        int result = num1 + num2;
        SumCalculateResponse response = SumCalculateResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Streaming Server
    @Override
    public void deviceCalculate(DeviceCalculateRequest request, StreamObserver<DeviceCalculateResponse> responseObserver) {
        int num = request.getNum();
        try{
            int k = 2;
            while (num > 1){
                if(num%k==0){
                    num/=k;
                    DeviceCalculateResponse response = DeviceCalculateResponse.newBuilder().setResult(k).build();
                    responseObserver.onNext(response);
                    Thread.sleep(500L);
                }
                else{
                    k+=1;
                }
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            responseObserver.onCompleted();
        }

    }

    //Streaming Client
    @Override
    public StreamObserver<MedianCalculateRequest> medianCalculate(StreamObserver<MedianCalculateResponse> responseObserver) {
        // Place that take request
        StreamObserver<MedianCalculateRequest> requestStreamObserver = new StreamObserver<MedianCalculateRequest>() {
            double sum = 0;
            int count = 0;
            @Override
            public void onNext(MedianCalculateRequest value) {
                // Take request one by one
                sum += value.getNum();
                count += 1;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                MedianCalculateResponse response = MedianCalculateResponse.newBuilder().setResult(sum/count).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }

    // Bi-Di Streaming
    @Override
    public StreamObserver<FindMaxRequest> findMaxCalculate(StreamObserver<FindMaxRespone> responseObserver) {
        StreamObserver<FindMaxRequest> requestStreamObserver = new StreamObserver<FindMaxRequest>() {
            ArrayList<Integer> arr = new ArrayList<Integer>();
            @Override
            public void onNext(FindMaxRequest value) {
                arr.add(value.getNum());
                int result = Collections.max(arr);
                responseObserver.onNext(
                        FindMaxRespone.newBuilder().setResult(result).build()
                );
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }

    //Catch Error
    @Override
    public void squareError(SquareErrorRequest request, StreamObserver<SquareErrorResponse> responseObserver) {
        int num = request.getNum();
        if(num >= 0){
            double result = Math.sqrt(num);
            responseObserver.onNext(
                    SquareErrorResponse.newBuilder().setResult(result).build()
            );
            responseObserver.onCompleted();
        }
        else{
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("The number being sent is not a positive")
                    .augmentDescription("The number is: " + num)
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void tradeWithDeadline(TradeWithDeadlineRequest request, StreamObserver<TradeWithDeadlineResponse> responseObserver) {
        Context current = Context.current();
        try {
            for(int i =0;i <3;i++) {
                if(!current.isCancelled()) {
                    Thread.sleep(100);
                }
                else{
                    return;
                }
            }
            int num = request.getNum();
            responseObserver.onNext(
                    TradeWithDeadlineResponse.newBuilder().setResult(num*2).build()
            );
            System.out.println("Successfully trading item " + num + " !");
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
