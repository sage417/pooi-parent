package app.pooi.workflow.infrastructure.configuration.flowable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.interceptor.CreateUserTaskAfterContext;
import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;
import org.flowable.engine.interceptor.CreateUserTaskInterceptor;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

@Slf4j
class FlowableCreateUserTaskInterceptor implements CreateUserTaskInterceptor {
    @Override
    public void beforeCreateUserTask(CreateUserTaskBeforeContext context) {
        log.info("taskName: {}", context.getName());
    }

    @Override
    public void afterCreateUserTask(CreateUserTaskAfterContext context) {
        log.info("taskId: {}", context.getUserTask().getId());

        TaskEntity taskEntity = context.getTaskEntity();

        // check task entity assignee and candidates
        if (StringUtils.isEmpty(taskEntity.getAssignee()) && CollectionUtils.isEmpty(taskEntity.getCandidates())) {
            log.warn("taskId: {} has no assignee and candidates", context.getUserTask().getId());
        }

    }
}
