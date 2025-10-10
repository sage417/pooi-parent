package app.pooi.workflow.application.service;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.application.converter.UserProcessInstanceItemResultMapper;
import app.pooi.workflow.application.result.UserFinishProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserStartProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserTodoProcessInstanceItemResult;
import com.google.common.collect.Ordering;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserProcessInstanceQueryAppService {

    @Resource
    private ApplicationInfoHolder applicationInfoHolder;

    @Resource
    private HistoryService historyService;

    @Resource
    private UserProcessInstanceItemResultMapper userProcessInstanceItemResultMapper;

    @Resource
    private TaskService taskService;

    public List<UserStartProcessInstanceItemResult> queryUserStartInstances(String userId, int pageNo, int pageSize) {

        var historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(applicationInfoHolder.getApplicationCode())
                .startedBy(userId)
                .orderByProcessInstanceStartTime()
                .desc()
                .listPage((pageNo - 1) * pageSize, pageSize);

        var unfinishedInstanceIds = historicProcessInstances.stream()
                .filter(inst -> inst.getEndTime() == null)
                .map(HistoricProcessInstance::getId)
                .toList();

        var unfinishedTasks = taskService.createTaskQuery()
                .processInstanceIdIn(unfinishedInstanceIds)
                .taskTenantId(applicationInfoHolder.getApplicationCode())
                .includeIdentityLinks()
                .list();

        var taskMapByInstanceId = unfinishedTasks.stream().collect(Collectors.groupingBy(Task::getProcessInstanceId));

        return historicProcessInstances.stream()
                .map(inst -> {
                    var item = userProcessInstanceItemResultMapper.convert(inst);
                    List<Task> tasks = taskMapByInstanceId.get(item.getProcessInstanceId());
                    item.setCurrentNodeName(tasks.stream().map(Task::getName).collect(Collectors.joining(",")));
                    return item;
                })
                .toList();
    }

    public List<UserTodoProcessInstanceItemResult> queryUserTodoProcessInstances(String userId, int pageNo, int pageSize) {
        var tasks = taskService.createTaskQuery()
                .taskTenantId(applicationInfoHolder.getApplicationCode())
                .taskCandidateOrAssigned(userId)
                .orderByTaskCreateTime()
                .desc()
                .listPage((pageNo - 1) * pageSize, pageSize);

        var instanceIds = tasks.stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toSet());

        var historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(applicationInfoHolder.getApplicationCode())
                .processInstanceIds(instanceIds)
                .list();

        var processInstanceMapById = historicProcessInstances.stream().collect(Collectors.toMap(HistoricProcessInstance::getId, Function.identity()));

        return tasks.stream()
                .map(task -> {
                    var item = userProcessInstanceItemResultMapper.convert(task);
                    var instanceId = processInstanceMapById.get(item.getProcessInstanceId());
                    userProcessInstanceItemResultMapper.update(item, instanceId);
                    return item;
                })
                .toList();
    }

    public List<UserFinishProcessInstanceItemResult> queryUserFinishedProcessInstances(String userId, int pageNo, int pageSize) {
        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .taskTenantId(applicationInfoHolder.getApplicationCode())
                .taskAssignee(userId)
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .listPage((pageNo - 1) * pageSize, pageSize);

        if (CollectionUtils.isEmpty(historicTasks)) {
            return Collections.emptyList();
        }

        var instanceIds = historicTasks.stream().map(TaskInfo::getProcessInstanceId).collect(Collectors.toSet());

        var historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(applicationInfoHolder.getApplicationCode())
                .processInstanceIds(instanceIds)
                .list();

        Map<String, List<HistoricTaskInstance>> taskGroupByInstanceId = historicTasks.stream().collect(Collectors.groupingBy(HistoricTaskInstance::getProcessInstanceId));

        return historicProcessInstances.stream()
                .map(instance -> {
                    var itemResult = userProcessInstanceItemResultMapper.convertFinishResult(instance);

                    var historicTaskInstances = taskGroupByInstanceId.get(instance.getId());
                    Optional<HistoricTaskInstance> lastFinishTaskInstance = historicTaskInstances.stream()
                            .max(Ordering.natural().onResultOf(HistoricTaskInstance::getEndTime));
                    userProcessInstanceItemResultMapper.updateFinishResult(itemResult, lastFinishTaskInstance.orElse(null));
                    return itemResult;
                })
                .toList();
    }
}
