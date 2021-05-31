package com.github.simplesteph.grpc.greet.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // get something u want
        Greeting greeting = request.getGreeting();
        String first_name = greeting.getFirstName();

        // creat the response
        String result = "Hello " + first_name;
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        // send the response
        responseObserver.onNext(response);

        // Complete the RPC call
        responseObserver.onCompleted();
        //super.greet(request, responseObserver);
    }
}
