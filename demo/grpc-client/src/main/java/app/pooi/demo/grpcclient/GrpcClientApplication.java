package app.pooi.demo.grpcclient;

import app.pooi.rpc.workflow.stubs.HelloWorldRequest;
import app.pooi.rpc.workflow.stubs.HelloWorldServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
@RestController
public class GrpcClientApplication {

    @GrpcClient("pooi-workflow-core")
    private HelloWorldServiceGrpc.HelloWorldServiceBlockingStub stub;


    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
    }


    @GetMapping("/sayHello")
    public String greeting(@RequestParam String name) {
        HelloWorldRequest request = HelloWorldRequest.newBuilder()
                .setName(name)
                .build();
        return stub.sayHello(request).getGreeting();
    }


}
