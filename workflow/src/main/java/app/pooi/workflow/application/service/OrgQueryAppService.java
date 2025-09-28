package app.pooi.workflow.application.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrgQueryAppService {

    public String findSuperior(DelegateExecution execution, String query, String role) {
        log.info("querySuperior {} {}", execution.getCurrentFlowElement(), role);
        return "superior";
    }
}
