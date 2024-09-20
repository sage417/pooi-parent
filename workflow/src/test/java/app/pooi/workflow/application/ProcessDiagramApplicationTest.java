package app.pooi.workflow.application;

import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.conf.TestRedisConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {TestRedisConfiguration.class})
class ProcessDiagramApplicationTest {

    @Autowired
    private ProcessDiagramApplication processDiagramApplication;

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void diagram() {
        processDiagramApplication.diagram(TENANT_APP_1, "articleReview", null);

    }
}