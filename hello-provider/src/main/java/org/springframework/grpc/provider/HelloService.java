package org.springframework.grpc.provider;

import io.github.rocwg.grpc.contracts.hello.v1.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;

@Service
public class HelloService extends HelloServiceGrpc.HelloServiceImplBase {

	private static Log log = LogFactory.getLog(HelloService.class);

	@Override
	public void sayHello(SayHelloRequest req, StreamObserver<SayHelloResponse> responseObserver) {
		log.info("Hello " + req.getName());
		if (req.getName().startsWith("error")) {
			throw new IllegalArgumentException("Bad name: " + req.getName());
		}
		if (req.getName().startsWith("internal")) {
			throw new RuntimeException();
		}
        SayHelloResponse reply = SayHelloResponse.newBuilder().setMessage("Hello ==> " + req.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}

	@Override
	public void streamHello(StreamHelloRequest req, StreamObserver<StreamHelloResponse> responseObserver) {
		log.info("Hello " + req.getName());
		int count = 0;
		while (count < 10) {
            StreamHelloResponse reply = StreamHelloResponse.newBuilder().setMessage("Hello(" + count + ") ==> " + req.getName()).build();
			responseObserver.onNext(reply);
			count++;
			try {
				Thread.sleep(1000L);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				responseObserver.onError(e);
				return;
			}
		}
		responseObserver.onCompleted();
	}

}
