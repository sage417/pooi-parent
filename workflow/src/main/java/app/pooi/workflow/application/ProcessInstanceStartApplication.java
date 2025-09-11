package app.pooi.workflow.application;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ProcessInstanceStartApplication {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;


    public void processInstanceStart(String processDefinitionKey, Integer processDefinitionVersion) {
        String applicationCode = applicationInfoHolder.getApplicationCode();

        if (processDefinitionVersion == null) {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, "", null, applicationCode);
            return;
        } else {
//            DeploymentManager deploymentManager = CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager();
//            ProcessDefinition definition = deploymentManager.findDeployedProcessDefinitionByKeyAndVersionAndTenantId(
//                    processDefinitionKey, processDefinitionVersion, "");
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionTenantId(applicationCode)
                    .processDefinitionKey(processDefinitionKey)
                    .processDefinitionVersion(processDefinitionVersion)
                    .singleResult();

            if (definition != null) {
                // inherit tenant_id from definition
                ProcessInstance processInstance = runtimeService.startProcessInstanceById(definition.getId(), "", null);
            }
            return;
        }
    }

}
