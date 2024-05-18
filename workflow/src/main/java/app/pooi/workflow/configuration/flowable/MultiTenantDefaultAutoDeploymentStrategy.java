package app.pooi.workflow.configuration.flowable;

import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.common.spring.CommonAutoDeploymentProperties;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.spring.configurator.DefaultAutoDeploymentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

class MultiTenantDefaultAutoDeploymentStrategy extends DefaultAutoDeploymentStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAutoDeploymentStrategy.class);

    private TenantInfoHolder tenantInfoHolder;

    @Override
    protected String getDeploymentMode() {
        return "default-per-tenant";
    }

    public MultiTenantDefaultAutoDeploymentStrategy() {
    }

    public MultiTenantDefaultAutoDeploymentStrategy(CommonAutoDeploymentProperties deploymentProperties) {
        super(deploymentProperties);
    }

    @Override
    protected void deployResourcesInternal(String deploymentNameHint, Resource[] resources, ProcessEngine engine) {
        super.deployResourcesInternal(deploymentNameHint, resources, engine);

        if (engine instanceof MultiSchemaMultiTenantProcessEngineConfiguration) {
            this.tenantInfoHolder = ((MultiSchemaMultiTenantProcessEngineConfiguration) engine).getTenantInfoHolder();
        }

        if (this.tenantInfoHolder == null) {
            return;
        }
        RepositoryService repositoryService = engine.getRepositoryService();

        // Create a single deployment for all resources using the name hint as the literal name
        final DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().enableDuplicateFiltering().tenantId(this.tenantInfoHolder.getCurrentTenantId()).name(deploymentNameHint);

        for (final Resource resource : resources) {
            addResource(resource, deploymentBuilder);
        }

        try {
            deploymentBuilder.deploy();
        } catch (RuntimeException e) {
            if (isThrowExceptionOnDeploymentFailure()) {
                throw e;
            } else {
                LOGGER.warn("Exception while autodeploying process definitions. "
                        + "This exception can be ignored if the root cause indicates a unique constraint violation, "
                        + "which is typically caused by two (or more) servers booting up at the exact same time and deploying the same definitions. ", e);
            }
        }
    }
}
