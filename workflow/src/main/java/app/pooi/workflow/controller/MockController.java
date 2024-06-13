package app.pooi.workflow.controller;

import app.pooi.model.rest.CommonResult;
import app.pooi.model.workflow.event.EventPayload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/mock")
@RestController
public class MockController {

    @PostMapping("/event")
    public CommonResult<Void> processEvent(@RequestBody EventPayload eventPayload) {
        return CommonResult.success(null);
    }
}
