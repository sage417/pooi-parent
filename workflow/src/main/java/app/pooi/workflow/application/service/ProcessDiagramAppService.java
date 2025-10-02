package app.pooi.workflow.application.service;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.domain.model.workflow.diagram.ProcessDiagramElement;
import app.pooi.workflow.util.BpmnModelUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ProcessDiagramAppService {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private HistoryService historyService;

    public List<ProcessDiagramElement> travelProcessInstance(@NonNull String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .processInstanceTenantId(applicationInfoHolder.getApplicationCode())
                .singleResult();

        if (historicProcessInstance == null) {
            return Collections.emptyList();
        }

        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        StartEvent startEvent = BpmnModelUtil.findFirstStartEvent(bpmnModel);

        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityTenantId(applicationInfoHolder.getApplicationCode())
                .list();

        BpmnModelUtil.travel(bpmnModel, startEvent.getId(), null, flowElement -> {

        });


        return Collections.emptyList();
    }


    public List<ProcessDiagramElement> travel(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);

        ArrayList<ProcessDiagramElement> result = new ArrayList<>();
        BpmnModelUtil.travel(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(), null,
                flowElement -> result.add(new ProcessDiagramElement(flowElement.getId(), flowElement.getName())));
        return result;
    }

    public List<ProcessDiagramElement> bfs(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);
        ArrayList<ProcessDiagramElement> result = new ArrayList<>();
        BpmnModelUtil.bfs(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(),
                flowElement -> result.add(new ProcessDiagramElement(flowElement.getId(), flowElement.getName())));
        return result;
    }

    public List<ProcessDiagramElement> dfs(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);
        ArrayList<ProcessDiagramElement> result = new ArrayList<>();
        BpmnModelUtil.dfs(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(),
                flowElement -> result.add(new ProcessDiagramElement(flowElement.getId(), flowElement.getName())));
        return result;
    }


    private BpmnModel findBpmnModel(@NonNull String defKey, Integer version) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(defKey)
                .processDefinitionTenantId(applicationInfoHolder.getApplicationCode());
        if (version != null) {
            processDefinitionQuery = processDefinitionQuery.processDefinitionVersion(version);
        } else {
            processDefinitionQuery = processDefinitionQuery.latestVersion();
        }

        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        return repositoryService.getBpmnModel(processDefinition.getId());
    }
}
