package app.pooi.workflow.application;

import app.pooi.workflow.util.BpmnModelUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
public class ProcessDiagramApplication {

    @Resource
    private RepositoryService repositoryService;

    public void diagram(@NonNull String tenantId, @NonNull String defKey, Integer version) {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(defKey)
                .processDefinitionTenantId(tenantId);
        if (version != null) {
            processDefinitionQuery = processDefinitionQuery.processDefinitionVersion(version);
        } else {
            processDefinitionQuery = processDefinitionQuery.latestVersion();
        }

        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        Process mainProcess = bpmnModel.getMainProcess();
        List<StartEvent> startEvents = mainProcess.findFlowElementsOfType(StartEvent.class, false);
        BpmnModelUtil.travel(bpmnModel, startEvents.getFirst().getId(), null, new Consumer<FlowElement>() {
            @Override
            public void accept(FlowElement flowElement) {
                log.info("{} - {}", flowElement.getId(), flowElement.getName());
            }
        });
    }
}
