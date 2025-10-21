package app.pooi.workflow.application.service;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProcessInstanceMoveAppService {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private CommentService commentService;

    @Resource
    private TaskService taskService;

    @Transactional
    public void rollback(String processInstanceId, String sourceActivityId, String targetActivityId) {

        List<Execution> currentExecutions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .onlyChildExecutions()
                .list();

        Optional<Execution> optSourceExecution = currentExecutions.stream()
                .filter(execution -> StringUtils.equals(execution.getActivityId(), sourceActivityId))
                .findFirst();

        if (optSourceExecution.isEmpty()) {
            log.warn("sourceActivityId: {} not in current child executions", sourceActivityId);
            return;
        }
        Execution sourceExecution = optSourceExecution.get();

        // sourceTask complete after changeState need to query before changeState()
        Task sourceTask = taskService.createTaskQuery()
                .executionId(sourceExecution.getId())
                .processInstanceId(processInstanceId)
                .taskTenantId(sourceExecution.getTenantId())
                .singleResult();

        List<String> executionIds = currentExecutions.stream().map(Execution::getId).collect(Collectors.toList());

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveExecutionsToSingleActivityId(executionIds, targetActivityId)
                .changeState();

        Comment comment = sourceTask == null ? commentService.createFormExecution((ExecutionEntity) sourceExecution) :
                commentService.createFromTask(sourceTask, "ROLLBACK");
        comment.setType("ROLLBACK");
        commentService.recordComment(comment);
    }
}
