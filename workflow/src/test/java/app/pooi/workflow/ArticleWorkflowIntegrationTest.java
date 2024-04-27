package app.pooi.workflow;


import java.util.HashMap;
import java.util.Map;

import app.pooi.workflow.conf.TestRedisConfiguration;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {TestRedisConfiguration.class})
class ArticleWorkflowIntegrationTest {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedissonClient redissonClient;

    @Test
    @Deployment(resources = { "processes/article-workflow.bpmn20.xml" })
    void articleApprovalTest() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("author", "test@baeldung.com");
        variables.put("url", "http://baeldung.com/dummy");
        runtimeService.startProcessInstanceByKey("articleReview", variables);
        Task task = taskService.createTaskQuery()
                .singleResult();
        assertEquals("Review the submitted tutorial", task.getName());
        variables.put("approved", true);
        taskService.complete(task.getId(), variables);
        assertEquals(0, runtimeService.createProcessInstanceQuery()
                .count());
    }

    @Test
    void fairLockTest() {
        RLock fairlock = redissonClient.getFairLock("fairlock");
        long tid = RandomUtils.nextLong();
        RFuture<Void> rFuture = fairlock.lockAsync(tid);
        rFuture.whenComplete((res, exception) -> {
            assertTrue(fairlock.isHeldByThread(tid));
            fairlock.unlockAsync();
        });
    }
}
