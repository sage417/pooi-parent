package app.pooi.workflow.application;

import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.application.entity.FlowElementEntity;
import app.pooi.workflow.application.service.ProcessDiagramAppService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

import static app.pooi.workflow.TenantInfoHolderExtension.TENANT_APP_1;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {})
class ProcessDiagramApplicationTest {

    @Resource
    private ProcessDiagramAppService processDiagramApplication;

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void bfs() {
        List<FlowElementEntity> flowElementEntities = processDiagramApplication.bfs("articleReview", null);
        Assertions.assertThat(flowElementEntities).hasSize(12);
    }

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void dfs() {
        List<FlowElementEntity> flowElementEntities = processDiagramApplication.dfs("articleReview", null);
        Assertions.assertThat(flowElementEntities).hasSize(12);
    }

    @Test
    @SneakyThrows
    @Deployment(resources = {"processes/article-workflow.bpmn20.xml"}, tenantId = TENANT_APP_1)
    void travel() {
        List<FlowElementEntity> flowElementEntities = processDiagramApplication.travel("articleReview", null);
        Assertions.assertThat(flowElementEntities).hasSize(12);
    }
}