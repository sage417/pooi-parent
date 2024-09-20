package app.pooi.workflow.application;

import app.pooi.common.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.application.entity.FlowElementEntity;
import app.pooi.workflow.util.BpmnModelUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProcessDiagramApplication {

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    public List<FlowElementEntity> diagram(@NonNull String defKey, Integer version) {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(defKey)
                .processDefinitionTenantId(applicationInfoHolder.getApplicationCode());
        if (version != null) {
            processDefinitionQuery = processDefinitionQuery.processDefinitionVersion(version);
        } else {
            processDefinitionQuery = processDefinitionQuery.latestVersion();
        }

        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        Process mainProcess = bpmnModel.getMainProcess();
        List<StartEvent> startEvents = mainProcess.findFlowElementsOfType(StartEvent.class, false);
        ArrayList<FlowElementEntity> result = new ArrayList<>();
        BpmnModelUtil.travel(bpmnModel, startEvents.getFirst().getId(), null,
                flowElement -> result.add(new FlowElementEntity(flowElement.getId(), flowElement.getName())));
        return result;
    }
}
