package app.pooi.demo.grpcclient;

import app.pooi.modules.workflow.stubs.HelloWorldServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GrpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
    }

    @GrpcClient("pooi-workflow-core")
    private HelloWorldServiceGrpc.HelloWorldServiceStub stub;


}
