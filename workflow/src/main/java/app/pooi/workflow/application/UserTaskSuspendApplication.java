package app.pooi.workflow.application;

import app.pooi.workflow.applicationsupport.workflowcomment.AddCommentBO;
import app.pooi.workflow.applicationsupport.workflowcomment.CommentSupport;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.engine.ManagementService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.SuspensionStateUtil;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.Task;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserTaskSuspendApplication {

    @Resource
    private TaskService taskService;

    @Resource
    private CommentSupport commentSupport;

    @Resource
    private ManagementService managementService;


    public void suspend(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return;
        }

        managementService.executeCommand((Command<Void>) commandContext -> {
            SuspensionStateUtil.setSuspensionState((TaskEntity) task, SuspensionState.SUSPENDED);
            CommandContextUtil.getTaskService().updateTask((TaskEntity) task, false);
            return null;
        });

        AddCommentBO addCommentBO = commentSupport.createFromTask(task);
        addCommentBO.setType("SUSPEND");
        commentSupport.recordComment(addCommentBO);
    }

    public void active(String taskId) {

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return;
        }

        managementService.executeCommand((Command<Void>) commandContext -> {
            SuspensionStateUtil.setSuspensionState((TaskEntity) task, SuspensionState.ACTIVE);
            CommandContextUtil.getTaskService().updateTask((TaskEntity) task, false);
            return null;
        });

        AddCommentBO addCommentBO = commentSupport.createFromTask(task);
        addCommentBO.setType("ACTIVE");
        commentSupport.recordComment(addCommentBO);

    }

}
