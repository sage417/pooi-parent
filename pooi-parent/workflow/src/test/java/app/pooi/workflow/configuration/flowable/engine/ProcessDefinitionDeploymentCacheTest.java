package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.workflow.TenantInfoHolderExtension;
import app.pooi.workflow.infrastructure.configuration.flowable.engine.ProcessDefinitionDeploymentCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(TenantInfoHolderExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@SpringBootTest(classes = {})
class ProcessDefinitionDeploymentCacheTest {

    @Resource
    private ProcessDefinitionDeploymentCache<Object> processDefinitionDeploymentCache;

    @Test
    void add() {
        processDefinitionDeploymentCache.add("id", new Object());
        assertTrue(processDefinitionDeploymentCache.contains("id"));
    }

    @SneakyThrows
    @Test
    void remove() {
        processDefinitionDeploymentCache.add("id", new Object());
        assertEquals(1, processDefinitionDeploymentCache.size());
        processDefinitionDeploymentCache.remove("id");
        TimeUnit.SECONDS.sleep(1L);
        assertEquals(0, processDefinitionDeploymentCache.size());
    }

    @SneakyThrows
    @Test
    void clear() {
        processDefinitionDeploymentCache.add("id", new Object());
        assertEquals(1, processDefinitionDeploymentCache.size());
        processDefinitionDeploymentCache.clear();
        TimeUnit.SECONDS.sleep(1L);
        assertEquals(0, processDefinitionDeploymentCache.size());
    }
}