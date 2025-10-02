package app.pooi.workflow.application.service;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.model.workflow.diagram.ProcessDiagramElement;
import app.pooi.workflow.domain.service.comment.CommentService;
import app.pooi.workflow.util.BpmnModelUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.SequenceFlow;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessDiagramAppService {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private HistoryService historyService;

    @Resource
    private CommentService commentService;


    public List<Comment> listComments(@NonNull String processInstanceId) {
        return commentService.listByInstanceId(processInstanceId);
    }

    public List<ProcessDiagramElement> travelProcessInstanceTimeLine(@NonNull String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .processInstanceTenantId(applicationInfoHolder.getApplicationCode())
                .singleResult();

        if (historicProcessInstance == null) {
            return Collections.emptyList();
        }


        List<Comment> comments = commentService.listByInstanceId(processInstanceId);

        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        StartEvent startEvent = BpmnModelUtil.findFirstStartEvent(bpmnModel);

        List<HistoricActivityInstance> sequenceFlowActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityTenantId(applicationInfoHolder.getApplicationCode())
                .activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW)
                .list();
        Set<SequenceFlow> reachableSequenceFLows = findSequenceFlowsFromHistoricActivity(bpmnModel, sequenceFlowActivityInstances);

        BpmnModelUtil.travelCond(bpmnModel, startEvent.getId(), null, reachableSequenceFLows, flowElement -> {

        });

        return Collections.emptyList();
    }

    private Set<SequenceFlow> findSequenceFlowsFromHistoricActivity(BpmnModel bpmnModel, List<HistoricActivityInstance> activityInstances) {

        Set<String> sequenceFLowActivityIds = activityInstances.stream()
                .filter(e -> BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW.equals(e.getActivityType()))
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toSet());

        List<SequenceFlow> sequenceFlowList = bpmnModel.getMainProcess().findFlowElementsOfType(SequenceFlow.class);

        return sequenceFlowList.stream()
                .filter(seq -> sequenceFLowActivityIds.contains(BpmnModelUtil.getSequenceFlowActivityId(seq)))
                .collect(Collectors.toSet());
    }


    public List<ProcessDiagramElement> travel(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);

        ArrayList<ProcessDiagramElement> result = new ArrayList<>();
        BpmnModelUtil.travel(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(), null,
                flowElement -> result.add(new ProcessDiagramElement(flowElement.getId(), flowElement.getName(), null)));
        return result;
    }

    public List<ProcessDiagramElement> bfs(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);
        ArrayList<ProcessDiagramElement> result = new ArrayList<>();
        BpmnModelUtil.bfs(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(),
                flowElement -> result.add(new ProcessDiagramElement(flowElement.getId(), flowElement.getName(), null)));
        return result;
    }

    public List<ProcessDiagramElement> dfs(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);
        ArrayList<ProcessDiagramElement> result = new ArrayList<>();
        BpmnModelUtil.dfs(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(),
                flowElement -> result.add(new ProcessDiagramElement(flowElement.getId(), flowElement.getName(), null)));
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
