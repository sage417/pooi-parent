package app.pooi.workflow.application.service;

import app.pooi.basic.expection.BusinessException;
import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.domain.result.ProcessInstanceStartResult;
import app.pooi.workflow.domain.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProcessInstanceStartAppService {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private CommentService commentService;

    @Transactional
    public ProcessInstanceStartResult start(String processDefinitionKey, Integer processDefinitionVersion,
                                            String businessKey, Map<String, Object> variables, String startUserId) {

        String applicationCode = applicationInfoHolder.getApplicationCode();

        if (variables == null) {
            variables = new HashMap<>();
        }

        Authentication.setAuthenticatedUserId(startUserId);

        ProcessInstance processInstance = null;

        if (processDefinitionVersion == null) {
            processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, businessKey, variables, applicationCode);
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
            processInstance = runtimeService.startProcessInstanceById(definition.getId(), businessKey, variables);
        }

        commentService.recordComment(commentService.createFromInstance(processInstance));

        return ProcessInstanceStartResult.builder()
                .processInstanceId(processInstance.getId())
                .build();
    }

}
