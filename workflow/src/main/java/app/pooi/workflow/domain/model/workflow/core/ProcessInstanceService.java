package app.pooi.workflow.domain.model.workflow.core;

import app.pooi.basic.expection.BusinessException;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class ProcessInstanceService {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;


    public ProcessInstance startInstance(String processDefinitionKey, Integer processDefinitionVersion, String businessKey,
                                         Map<String, Object> variables, String applicationCode) {

        if (processDefinitionVersion == null) {
            return runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, businessKey, variables, applicationCode);
        } else {
            ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionTenantId(applicationCode)
                    .processDefinitionKey(processDefinitionKey)
                    .processDefinitionVersion(processDefinitionVersion)
                    .singleResult();

            if (definition == null) {
                throw new BusinessException("bizExp.process.definition.not_found", processDefinitionKey, processDefinitionVersion);
            }

            // inherit tenant_id from definition
            return runtimeService.startProcessInstanceById(definition.getId(), businessKey, variables);
        }
    }
}
