package app.pooi.workflow;


import app.pooi.workflow.conf.TestRedisConfiguration;
import app.pooi.workflow.repository.workflow.EventRecordRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {TestRedisConfiguration.class})
class ArticleWorkflowIntegrationTest {
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private EventRecordRepository eventRecordRepository;

    @SneakyThrows
    @Test
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void articleApprovalTest() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");
        runtimeService.startProcessInstanceByKeyAndTenantId("articleReview", variables, TENANT_APP_1);
        Task task = taskService.createTaskQuery()
                .singleResult();
        assertEquals("Review the submitted tutorial", task.getName());
        variables.put("approved", true);
        taskService.setAssignee(task.getId(), "target");
        taskService.complete(task.getId(), variables);
        assertEquals(0, runtimeService.createProcessInstanceQuery()
                .count());
        TimeUnit.SECONDS.sleep(5);
        // wait
        assertEquals(15, eventRecordRepository.count(), "");
    }

    @Test
    void fairLockTest() {
        RLock fairlock = redissonClient.getFairLock("fair_lock");
        long tid = RandomUtils.nextLong();
        RFuture<Void> rFuture = fairlock.lockAsync(tid);
        rFuture.whenComplete((res, exception) -> {
            assertTrue(fairlock.isHeldByThread(tid));
            fairlock.unlockAsync();
        });
    }
}
