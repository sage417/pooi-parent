package app.pooi.workflow.interfaces.rest;

import app.pooi.basic.rest.CommonResult;
import app.pooi.basic.workflow.event.EventPayload;
import app.pooi.rpc.workflow.stubs.HelloWorldResponse;
import app.pooi.tenant.multitenancy.ApplicationInfo;
import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.application.ProcessDefinitionDeployApplication;
import app.pooi.workflow.application.ProcessInstanceStartApplication;
import app.pooi.workflow.application.eventpush.GenericGrpcInvoker;
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

    @Resource
    private GenericGrpcInvoker genericGrpcInvoker;


    @GetMapping("/say")
    public CommonResult<String> sayHelloWorld() {
        HelloWorldResponse response = genericGrpcInvoker.invokeByConfigKey("app1");
        return CommonResult.success(response.getGreeting());
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
