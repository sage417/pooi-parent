package app.pooi.workflow;

import app.pooi.workflow.conf.TestRedisConfiguration;
import app.pooi.workflow.query.AttachmentQuery;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {TestRedisConfiguration.class})
class CustomMapperQueryTest {


    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @SneakyThrows
    @Test
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void test() {
        assertEquals(0, new AttachmentQuery(processEngineConfiguration.getCommandExecutor()).count());
    }
}