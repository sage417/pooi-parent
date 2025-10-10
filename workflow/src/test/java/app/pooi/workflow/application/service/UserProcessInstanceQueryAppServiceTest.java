package app.pooi.workflow.application.service;

import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.application.result.ProcessInstanceStartResult;
import app.pooi.workflow.application.result.UserFinishProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserStartProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserTodoProcessInstanceItemResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {})
class UserProcessInstanceQueryAppServiceTest {

    @Resource
    private ProcessInstanceStartAppService processInstanceStartAppService;

    @Resource
    private UserProcessInstanceQueryAppService userProcessInstanceQueryAppService;

    @Resource
    private UserTaskOperationAppService userTaskOperationAppService;

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void queryUserStartInstances() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");

        ProcessInstanceStartResult instanceStartResult = processInstanceStartAppService.start("articleReview", null, "name", "", variables, "starter");

        List<UserStartProcessInstanceItemResult> itemResults = userProcessInstanceQueryAppService.queryUserStartInstances("starter", 1, 1);
        Assertions.assertThat(itemResults.size()).isEqualTo(1);
    }

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void queryUserTodoProcessInstances() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");

        ProcessInstanceStartResult instanceStartResult = processInstanceStartAppService.start("articleReview", null, "name", "", variables, "starter");

        List<UserTodoProcessInstanceItemResult> itemResults = userProcessInstanceQueryAppService.queryUserTodoProcessInstances("assignee1", 1, 1);
        Assertions.assertThat(itemResults.size()).isEqualTo(1);

    }

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void queryUserFinishedProcessInstances() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");

        ProcessInstanceStartResult instanceStartResult = processInstanceStartAppService.start("articleReview", null, "name", "", variables, "starter");

        List<UserTodoProcessInstanceItemResult> todos = userProcessInstanceQueryAppService.queryUserTodoProcessInstances("assignee1", 1, 1);
        variables.put("approved", true);

        String taskId = todos.getFirst().getTaskId();
        userTaskOperationAppService.completeTask(taskId, variables);

        List<UserFinishProcessInstanceItemResult> itemResults = userProcessInstanceQueryAppService.queryUserFinishedProcessInstances("assignee1", 1, 1);
        Assertions.assertThat(itemResults.size()).isEqualTo(1);
    }
}