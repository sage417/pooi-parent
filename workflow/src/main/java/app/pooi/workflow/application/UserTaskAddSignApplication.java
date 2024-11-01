package app.pooi.workflow.application;

import app.pooi.workflow.applicationsupport.workflowcomment.AddCommentBO;
import app.pooi.workflow.applicationsupport.workflowcomment.CommentSupport;
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
public class UserTaskAddSignApplication {

    @Resource
    private TaskService taskService;

    @Resource
    private CommentSupport commentSupport;


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

        AddCommentBO addCommentBO = commentSupport.createFromTask(task);
        addCommentBO.setType("ADD_SIGN");
        commentSupport.recordComment(addCommentBO);
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
