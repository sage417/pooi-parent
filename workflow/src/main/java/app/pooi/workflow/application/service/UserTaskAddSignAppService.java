package app.pooi.workflow.application.service;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.service.comment.CommentService;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserTaskAddSignAppService {

    @Resource
    private TaskService taskService;

    @Resource
    private CommentService commentService;


    @Transactional
    public void addSignTask(String taskId, Set<String> userIds) {
        // check params
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return;
        }

        taskService.setOwner(taskId, null);
        taskService.delegateTask(taskId, Const.AFTER_ADD_SIGN);

        List<Task> subTasks = userIds.stream().map(uId -> createSubTask(task, uId)).collect(Collectors.toList());
        taskService.bulkSaveTasks(subTasks);

        Comment addCommentBO = commentService.createFromTask(task, "ADD_SIGN");
        commentService.recordComment(addCommentBO);
    }

    private Task createSubTask(Task parentTask, String assignee) {
        return taskService.createTaskBuilder()
                .parentTaskId(parentTask.getId())
                .name(parentTask.getName())
                .taskDefinitionKey(parentTask.getTaskDefinitionKey())
                .taskDefinitionId(parentTask.getTaskDefinitionId())
                .tenantId(parentTask.getTenantId())
                .assignee(assignee)
                .create();

    }
}
