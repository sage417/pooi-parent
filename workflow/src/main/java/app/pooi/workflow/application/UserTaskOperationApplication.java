package app.pooi.workflow.application;

import org.flowable.engine.TaskService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Set;

@Component
public class UserTaskOperationApplication {

    @Resource
    private TaskService taskService;


    @Transactional(rollbackFor = Exception.class)
    public void addCirculate(String taskId, Set<String> userIds) {
        for (String userId : userIds) {
            taskService.addUserIdentityLink(taskId, userId, "circulate");
        }
    }
}
