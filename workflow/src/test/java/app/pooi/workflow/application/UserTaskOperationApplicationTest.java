package app.pooi.workflow.application;

import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.application.service.UserTaskAddSignAppService;
import app.pooi.workflow.application.service.UserTaskOperationAppService;
import app.pooi.workflow.domain.service.comment.CommentService;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {})
class UserTaskOperationApplicationTest {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private UserTaskOperationAppService userTaskOperationAppService;

    @Resource
    private UserTaskAddSignAppService userTaskAddSignAppService;

    @Resource
    private CommentService commentService;

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void addCirculate() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");
        runtimeService.startProcessInstanceByKeyAndTenantId("articleReview", variables, TENANT_APP_1);
        assertEquals(1, runtimeService.createProcessInstanceQuery().count());
        Task task = taskService.createTaskQuery()
                .singleResult();
        assertEquals("Review the submitted tutorial", task.getName());
        variables.put("approved", true);
        taskService.setAssignee(task.getId(), "target");

        userTaskOperationAppService.addCirculate(task.getId(), Sets.newHashSet("c1", "c2"));
        taskService.complete(task.getId(), variables);
        assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void addSignTasks() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");
        runtimeService.startProcessInstanceByKeyAndTenantId("articleReview", variables, TENANT_APP_1);
        assertEquals(1, runtimeService.createProcessInstanceQuery().count());
        Task task = taskService.createTaskQuery()
                .singleResult();
        assertEquals("Review the submitted tutorial", task.getName());
        variables.put("approved", true);
        taskService.setAssignee(task.getId(), "target");

        userTaskAddSignAppService.addSignTask(task.getId(), Sets.newHashSet("userId1"));
        assertEquals(2, taskService.createTaskQuery().count());
        assertEquals(0, taskService.createTaskQuery().taskAssignee("target").count());
        assertEquals(1, taskService.createTaskQuery().taskAssignee("userId1").count());
        assertEquals(1, commentService.listByInstanceId(task.getProcessInstanceId()).size());

        Task task1 = taskService.createTaskQuery().excludeSubtasks().singleResult();
        assertEquals(task1.getDelegationState(), DelegationState.PENDING);

        List<Task> subTasks = taskService.getSubTasks(task1.getId());
        for (Task subTask : subTasks) {
            assertEquals(taskService.getIdentityLinksForTask(subTask.getId()).size(), 1);
            userTaskOperationAppService.completeTask(subTask.getId(), variables);
        }
        assertEquals(0, taskService.createTaskQuery().count());

    }


}