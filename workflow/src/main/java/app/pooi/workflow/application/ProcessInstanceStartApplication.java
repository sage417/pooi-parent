package app.pooi.workflow.application;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ProcessInstanceStartApplication {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private RepositoryService repositoryService;


    public void processInstanceStart(String processDefinitionKey, Integer processDefinitionVersion) {
        if (processDefinitionVersion == null) {
            runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, "", null, "");
        }
        DeploymentManager deploymentManager = CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager();
        ProcessDefinition definition = deploymentManager.findDeployedProcessDefinitionByKeyAndVersionAndTenantId(
                processDefinitionKey, processDefinitionVersion, "");
        if (definition != null) {
            runtimeService.startProcessInstanceByKeyAndTenantId(definition.getId(), "", null, "");
        }
    }

}
