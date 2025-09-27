package app.pooi.workflow.application.service;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

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


    public void rollback(String processInstanceId, String sourceActivityId, String targetActivityId) {

        List<Execution> currentExecutions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .onlyChildExecutions()
                .list();

        Optional<Execution> optSourceExecution = currentExecutions.stream().filter(execution -> StringUtils.equals(execution.getActivityId(), sourceActivityId))
                .findFirst();
        if (optSourceExecution.isEmpty()) {
            log.warn("sourceActivityId: {} not in current child executions", sourceActivityId);
            return;
        }

        List<String> executionIds = currentExecutions.stream().map(Execution::getId).collect(Collectors.toList());

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveExecutionsToSingleActivityId(executionIds, targetActivityId)
                .changeState();

        Comment comment = createComment(optSourceExecution.get());
        comment.setType("ROLLBACK");
        commentService.recordComment(comment);

    }

    private Comment createComment(Execution execution) {
        Task task = taskService.createTaskQuery().executionId(execution.getId()).singleResult();
        if (task != null) {
            return commentService.createFromTask(task, "ROLLBACK");
        }
        return commentService.createFormExecution(execution);
    }

}
