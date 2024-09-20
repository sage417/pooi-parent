package app.pooi.workflow.application;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessInstanceStartApplication {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
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
