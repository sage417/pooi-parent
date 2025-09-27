package app.pooi.workflow.application;

import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.domain.service.comment.CommentService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {})
class ProcessInstanceMoveApplicationTest {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private CommentService commentService;

    @Resource
    private ProcessInstanceMoveApplication processInstanceMoveApplication;

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow2.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void rollback() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");
        runtimeService.startProcessInstanceByKeyAndTenantId("articleReview2", variables, TENANT_APP_1);
        assertEquals(1, runtimeService.createProcessInstanceQuery().count());
        Task task = taskService.createTaskQuery()
                .singleResult();
        assertEquals("Review the submitted tutorial", task.getName());
        variables.put("approved", true);
        taskService.setAssignee(task.getId(), "target");
        assertEquals(1, taskService.createTaskQuery().count());
        assertEquals(1, taskService.createTaskQuery().taskAssignee("target").count());

        taskService.complete(task.getId());
        assertEquals(1, taskService.createTaskQuery().count());
//        assertEquals(1, taskService.createTaskQuery().taskAssignee("target").count());

        processInstanceMoveApplication.rollback(task.getProcessInstanceId(), "reviewArticle2", task.getTaskDefinitionKey());
        assertEquals(1, taskService.createTaskQuery().taskDefinitionKey(task.getTaskDefinitionKey()).count());
    }
}