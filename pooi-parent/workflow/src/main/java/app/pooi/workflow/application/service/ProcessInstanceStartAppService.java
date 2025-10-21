package app.pooi.workflow.application.service;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.application.result.ProcessInstanceStartResult;
import app.pooi.workflow.domain.model.workflow.core.ProcessInstanceService;
import app.pooi.workflow.domain.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
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
    private CommentService commentService;

    @Resource
    private ProcessInstanceService processInstanceService;

    @Transactional
    public ProcessInstanceStartResult start(String processDefinitionKey, Integer processDefinitionVersion,
                                            String instanceName, String businessKey, Map<String, Object> variables, String startUserId) {

        String applicationCode = applicationInfoHolder.getApplicationCode();

        if (variables == null) {
            variables = new HashMap<>();
        }

        Authentication.setAuthenticatedUserId(startUserId);

        ProcessInstance processInstance = processInstanceService.startInstance(processDefinitionKey,
                processDefinitionVersion, instanceName, businessKey, variables, applicationCode);

        commentService.recordComment(commentService.createFromInstance(processInstance));

        return ProcessInstanceStartResult.builder()
                .processInstanceId(processInstance.getId())
                .build();
    }

}
