package app.pooi.workflow.application.service;

import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.application.converter.UserProcessInstanceItemResultMapper;
import app.pooi.workflow.application.result.UserCompletedProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserStartProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserTodoProcessInstanceItemResult;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.procquery.UserCompletedProcessTaskQuery;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.procquery.UserCompletedProcessTaskResult;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.procquery.UserProcQueryMapper;
import app.pooi.workflow.util.TaskEntityUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Ordering;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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

    @Resource
    private UserProcQueryMapper userProcQueryMapper;

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
                    var item = userProcessInstanceItemResultMapper.convertStartResult(inst);
                    List<Task> tasks = taskMapByInstanceId.get(item.getProcessInstanceId());
                    item.setCurrentNodeNames(getCurrentNodeNames(tasks));
                    item.setCurrentTaskCandidates(getTaskCandidates(tasks));
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
                    var item = userProcessInstanceItemResultMapper.convertTodoResult(task);
                    var instanceId = processInstanceMapById.get(item.getProcessInstanceId());
                    userProcessInstanceItemResultMapper.updateTodoResult(item, instanceId);
                    return item;
                })
                .toList();
    }

    public List<UserCompletedProcessInstanceItemResult> queryUserCompletedProcessInstances(String userId, int pageNo, int pageSize) {

        IPage<UserCompletedProcessTaskResult> page = userProcQueryMapper.selectUserCompletedProcessTaskIds(new Page<>(pageNo, pageSize),
                new UserCompletedProcessTaskQuery().setUserId(userId).setApplicationCode(applicationInfoHolder.getApplicationCode()));

        if (CollectionUtils.isEmpty(page.getRecords())) {
            return Collections.emptyList();
        }

        var instanceIds = page.getRecords().stream().map(UserCompletedProcessTaskResult::getProcessInstanceId).collect(Collectors.toSet());

        var historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceTenantId(applicationInfoHolder.getApplicationCode())
                .processInstanceIds(instanceIds)
                .list();

        List<String> completedTaskIds = page.getRecords().stream().map(UserCompletedProcessTaskResult::getTaskIds)
                .map(StringUtils::split)
                .flatMap(Arrays::stream)
                .toList();

        var historicTasks = historyService.createHistoricTaskInstanceQuery()
                .taskIds(completedTaskIds)
                .taskTenantId(applicationInfoHolder.getApplicationCode())
                .list();

        var historicTaskGroupByInstanceId = historicTasks.stream()
                .collect(Collectors.groupingBy(HistoricTaskInstance::getProcessInstanceId));

        // find unfinished instances
        var unfinishedInstanceIds = historicProcessInstances.stream().filter(instance -> instance.getEndTime() == null)
                .map(HistoricProcessInstance::getId)
                .collect(Collectors.toSet());


        Map<String, List<Task>> taskGroupByInstanceId = new HashMap<>();

        if (!CollectionUtils.isEmpty(unfinishedInstanceIds)) {
            var tasks = taskService.createTaskQuery()
                    .taskTenantId(applicationInfoHolder.getApplicationCode())
                    .processInstanceIdIn(unfinishedInstanceIds)
                    .list();

            taskGroupByInstanceId.putAll(tasks.stream()
                    .collect(Collectors.groupingBy(Task::getProcessInstanceId)));
        }

        return historicProcessInstances.stream()
                .map(instance -> {
                    var itemResult = userProcessInstanceItemResultMapper.convertFinishResult(instance);

                    var historicTaskInstances = historicTaskGroupByInstanceId.get(instance.getId());
                    Optional<HistoricTaskInstance> lastFinishTaskInstance = historicTaskInstances.stream()
                            .max(Ordering.natural().onResultOf(HistoricTaskInstance::getEndTime));
                    userProcessInstanceItemResultMapper.updateFinishResult(itemResult, lastFinishTaskInstance.orElse(null));

                    List<Task> unfinishedTasks = taskGroupByInstanceId.get(instance.getId());
                    itemResult.setCurrentNodeName(getCurrentNodeNames(unfinishedTasks));
                    itemResult.setCurrentTaskCandidates(getTaskCandidates(unfinishedTasks));
                    return itemResult;
                })
                .toList();
    }

    private static String getTaskCandidates(List<Task> tasks) {
        return CollectionUtils.emptyIfNull(tasks).stream().map(TaskEntityUtil::getAssigneeAndCandidates)
                .flatMap(Collection::stream).distinct().collect(Collectors.joining(","));
    }

    private static String getCurrentNodeNames(List<Task> tasks) {
        return CollectionUtils.emptyIfNull(tasks).stream().map(Task::getName).collect(Collectors.joining(","));
    }
}
