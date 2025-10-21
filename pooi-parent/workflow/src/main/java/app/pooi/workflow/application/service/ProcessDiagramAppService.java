package app.pooi.workflow.application.service;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.application.converter.ProcessTimelineItemResultMapper;
import app.pooi.workflow.application.result.ProcessTimelineItemResult;
import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.service.comment.CommentService;
import app.pooi.workflow.infrastructure.configuration.flowable.execution.FakeExecution;
import app.pooi.workflow.util.BpmnModelUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.*;
import org.flowable.engine.delegate.ReadOnlyDelegateExecution;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessDiagramAppService {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private PlatformTransactionManager transactionManager;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private CommentService commentService;

    @Resource
    private ProcessTimelineItemResultMapper processTimelineItemResultMapper;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private ManagementService managementService;

    public List<Comment> listComments(@NonNull String processInstanceId) {
        return commentService.listByInstanceId(processInstanceId);
    }

    @Transactional(readOnly = true)
    public List<ProcessTimelineItemResult> queryProcessDefinitionTimeLine(@NonNull String defKey, Integer defVersion) {

        List<ProcessTimelineItemResult> results = new ArrayList<>();

        BpmnModel bpmnModel = findBpmnModel(defKey, defVersion);

        if (bpmnModel == null) {
            return results;
        }

        ReadOnlyDelegateExecution execution = new FakeExecution(ImmutableMap.of());

        BpmnModelUtil.travel(bpmnModel, null, null, flowElement -> {
            ProcessTimelineItemResult item = null;
            if (flowElement instanceof UserTask userTask) {
                item = processTimelineItemResultMapper.convert(userTask, calculateUserTaskCandidates(userTask, execution));
            } else if (flowElement instanceof StartEvent startEvent) {
                item = processTimelineItemResultMapper.convert(startEvent);
            } else if (flowElement instanceof EndEvent endEvent) {
                item = processTimelineItemResultMapper.convert(endEvent);
            }
            if (item != null) {
                results.add(item);
            }
        });

        return results;
    }

    //    @Transactional(readOnly = true)
    public List<ProcessTimelineItemResult> queryProcessInstanceTimeLine(@NonNull String processInstanceId) {

        HistoricProcessInstance historicProcessInstance = null;
        List<Comment> comments = Collections.emptyList();
        List<Task> tasks = Collections.emptyList();
        Map<String, Object> variables = Collections.emptyMap();

        // 1. create DefaultTransactionDefinition
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setReadOnly(true);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        // 2. open transaction
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            do {
                // check processInstanceId
                historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .processInstanceTenantId(applicationInfoHolder.getApplicationCode())
                        .singleResult();

                if (historicProcessInstance == null) {
                    break;
                }

                // query comments
                comments = commentService.listByInstanceId(processInstanceId);

                if (historicProcessInstance.getEndTime() == null) {
                    // unfinished task
                    tasks = taskService.createTaskQuery()
                            .processInstanceId(processInstanceId)
                            .taskTenantId(applicationInfoHolder.getApplicationCode())
                            .includeIdentityLinks()
                            .list();

                    // variableInstances
                    variables = new HashMap<>(runtimeService.getVariables(historicProcessInstance.getId()));
                } else {
                    List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .excludeTaskVariables()
                            .list();
                    variables = new HashMap<>(historicVariableInstances.stream()
                            .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue)));
                }
            } while (false);

            // 3. commit transaction
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }

        if (historicProcessInstance == null) {
            return Collections.emptyList();
        }

        List<ProcessTimelineItemResult> results = new ArrayList<>();

        // convert to timeline item
        comments.stream().map(processTimelineItemResultMapper::convert)
                .forEach(results::add);

        // convert to timeline item
        tasks.stream()
                .map(processTimelineItemResultMapper::convert)
                .forEach(results::add);

        // make fake execution
        ReadOnlyDelegateExecution execution = new FakeExecution(variables);

        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        if (bpmnModel == null) {
            return results;
        }

        var futureFlowElements = new HashSet<FlowElement>();
        // search future flow elements
        tasks.stream().map(TaskInfo::getTaskDefinitionKey)
                .forEach(nodeId -> BpmnModelUtil.travel(bpmnModel, nodeId, null, futureFlowElements::add));

        // sort by travel order
        Ordering<FlowElement> flowElementOrdering = Ordering.explicit(BpmnModelUtil.getFlowElementsInOrder(bpmnModel));
        List<FlowElement> flowElements = flowElementOrdering.sortedCopy(futureFlowElements);

        // convert to timeline item
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask userTask) {
                results.add(processTimelineItemResultMapper.convert(userTask,
                        calculateUserTaskCandidates(userTask, execution)));
            } else if (flowElement instanceof EndEvent endEvent) {
                results.add(processTimelineItemResultMapper.convert(endEvent));
            }
        }

        return results;
    }

    private Set<String> calculateUserTaskCandidates(@NonNull UserTask userTask, @NonNull ReadOnlyDelegateExecution execution) {
        var candidates = Sets.<String>newHashSet();
        // assignee
        if (userTask.getAssignee() != null) {
            String assigneeValue = executeExpression(userTask.getAssignee(), execution);
            if (assigneeValue != null) {
                candidates.add(assigneeValue);
            }
        }
        // candidates
        userTask.getCandidateUsers().stream()
                .filter(StringUtils::isNotEmpty)
                .map(candidateUser -> (String) executeExpression(candidateUser, execution))
                .filter(StringUtils::isNotEmpty)
                .forEach(candidates::add);

        return candidates;
    }

    @SuppressWarnings("unchecked")
    private <T> T executeExpression(@NonNull String expression, @NonNull ReadOnlyDelegateExecution execution) {

        Object value = managementService.executeCommand(commandContext -> {
            ExpressionManager expressionManager = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager();
            return expressionManager.createExpression(expression).getValue(execution);
        });
        return (T) value;
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


    public List<ProcessTimelineItemResult> travel(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);

        ArrayList<ProcessTimelineItemResult> result = new ArrayList<>();
        BpmnModelUtil.travel(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(), null,
                flowElement -> result.add(ProcessTimelineItemResult.builder()
                        .nodeId(flowElement.getId())
                        .nodeName(flowElement.getName())
                        .build()));
        return result;
    }

    public List<ProcessTimelineItemResult> bfs(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);
        ArrayList<ProcessTimelineItemResult> result = new ArrayList<>();
        BpmnModelUtil.bfs(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(),
                flowElement -> result.add(ProcessTimelineItemResult.builder()
                        .nodeId(flowElement.getId())
                        .nodeName(flowElement.getName())
                        .build()));
        return result;
    }

    public List<ProcessTimelineItemResult> dfs(String defKey, Integer version) {
        BpmnModel bpmnModel = findBpmnModel(defKey, version);
        ArrayList<ProcessTimelineItemResult> result = new ArrayList<>();
        BpmnModelUtil.dfs(bpmnModel, BpmnModelUtil.findFirstStartEvent(bpmnModel).getId(),
                flowElement -> result.add(ProcessTimelineItemResult.builder()
                        .nodeId(flowElement.getId())
                        .nodeName(flowElement.getName())
                        .build()));
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
