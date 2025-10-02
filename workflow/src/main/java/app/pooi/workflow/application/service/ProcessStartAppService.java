package app.pooi.workflow.application.service;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class ProcessStartAppService {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private RuntimeService runtimeService;

    public String start(String definitionKey, Map<String, Object> variables, String startUserId) {
        Authentication.setAuthenticatedUserId(startUserId);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(definitionKey, variables, applicationInfoHolder.getApplicationCode());

        return processInstance.getId();
    }
}
