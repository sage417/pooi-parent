package app.pooi.workflow.conf;

import app.pooi.workflow.configuration.flowable.engine.ProcessDefinitionDeploymentCache;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRedisConfiguration {

    @Bean
    public DeploymentCache<ProcessDefinitionCacheEntry> deploymentCache() {
        return new ProcessDefinitionDeploymentCache<>();
    }
}
