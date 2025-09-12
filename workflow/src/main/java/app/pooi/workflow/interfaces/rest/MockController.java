package app.pooi.workflow.interfaces.rest;

import app.pooi.basic.rest.CommonResult;
import app.pooi.basic.workflow.event.EventPayload;
import app.pooi.rpc.workflow.stubs.HelloWorldRequest;
import app.pooi.rpc.workflow.stubs.HelloWorldServiceGrpc;
import app.pooi.tenant.multitenancy.ApplicationInfo;
import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.application.ProcessDefinitionDeployApplication;
import app.pooi.workflow.application.ProcessInstanceStartApplication;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/mock")
@RestController
public class MockController {

    @Resource
    private ProcessInstanceStartApplication processInstanceStartApplication;

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private ProcessDefinitionDeployApplication processDefinitionDeployApplication;

    @GrpcClient("helloWorldService")
    private HelloWorldServiceGrpc.HelloWorldServiceBlockingStub helloWorldServiceBlockingStub;


    @GetMapping("/say")
    public CommonResult<Void> sayHelloWorld() {
        HelloWorldRequest request = HelloWorldRequest.newBuilder()
                .setName("who you are")
                .build();
        helloWorldServiceBlockingStub.sayHello(request).getGreeting();
        return CommonResult.success(null);
    }


    @GetMapping("/start")
    public CommonResult<Void> start() {
        this.applicationInfoHolder.setApplicationInfo(new ApplicationInfo().setApplicationCode("app1"));
//        processDefinitionDeployApplication.deployResource("processes/article-workflow.bpmn20.xml", "articleReview", "test-process");
        this.processInstanceStartApplication.processInstanceStart("articleReview", 1);
        return CommonResult.success(null);
    }

    @PostMapping("/event")
    public CommonResult<Void> processEvent(@RequestBody EventPayload eventPayload) {
        return CommonResult.success(null);
    }
}
