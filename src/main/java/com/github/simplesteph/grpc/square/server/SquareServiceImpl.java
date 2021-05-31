package com.github.simplesteph.grpc.square.server;

import com.proto.square.*;
import io.grpc.stub.StreamObserver;

public class SquareServiceImpl extends SquareServiceGrpc.SquareServiceImplBase {
    @Override
    public void rectangle(RectangleRequest request, StreamObserver<RectangleResponse> responseObserver) {
        System.out.println("Hello, this is a Square Server");
        int chieuDai = request.getChieuDai();
        int chieuRong = request.getChieuRong();

        RectangleResponse rectangleResponse = RectangleResponse.newBuilder()
                .setResult(chieuDai*chieuRong)
                .build();

        responseObserver.onNext(rectangleResponse);
        responseObserver.onCompleted();
        //super.rectangle(request, responseObserver);
    }

    @Override
    public void rectangleManyTimes(RectangleManyTimesRequest request, StreamObserver<RectangleManyTimesResponse> responseObserver) {
        int num1 = request.getNum1();
        int num2 = request.getNum2();
        for(int i = 0; i < 5; i++){
            int result = num1*num2;
            num2 += 2;
            RectangleManyTimesResponse response = RectangleManyTimesResponse.newBuilder().setResult(result).build();
            responseObserver.onNext(response);
        }
    }
}
