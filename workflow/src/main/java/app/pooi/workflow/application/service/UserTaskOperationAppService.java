package app.pooi.workflow.application.service;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class UserTaskOperationAppService {


    @Resource
    private TaskService taskService;

    @Resource
    private CommentService commentService;

    @Transactional(rollbackFor = Exception.class)
    public void addCirculate(String taskId, Set<String> userIds) {
        for (String userId : userIds) {
            taskService.addUserIdentityLink(taskId, userId, "circulate");
        }
    }

    @Transactional
    public void completeTask(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return;
        }

        if (StringUtils.isEmpty(task.getAssignee())) {
            // TODO handle when assignee is empty
        }

        taskService.complete(taskId, variables);

        processParentTask(task.getParentTaskId(), variables);
        // record comment
        Comment addCommentBO = commentService.createFromTask(task, "COMPLETE_TASK");
        commentService.recordComment(addCommentBO);
    }

    private void processParentTask(String parentTaskId, Map<String, Object> variables) {
        List<Task> subTasks = StringUtils.isEmpty(parentTaskId) ?
                Collections.emptyList() :
                taskService.getSubTasks(parentTaskId);

        if (StringUtils.isNotEmpty(parentTaskId) && CollectionUtils.isEmpty(subTasks)) {
            Task parentTask = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
            if (parentTask.getDelegationState().equals(DelegationState.PENDING)) {
                taskService.resolveTask(parentTaskId, variables);
            }
            log.info("parent task assignee: {}", parentTask.getAssignee());
            if (Const.AFTER_ADD_SIGN.equals(parentTask.getAssignee())) {
                taskService.complete(parentTask.getId(), variables);
            }
        }
    }
}
