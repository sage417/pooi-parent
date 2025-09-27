package app.pooi.workflow.infrastructure.configuration.flowable;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.interceptor.CreateUserTaskAfterContext;
import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;
import org.flowable.engine.interceptor.CreateUserTaskInterceptor;

@Slf4j
class FlowableCreateUserTaskInterceptor implements CreateUserTaskInterceptor {
    @Override
    public void beforeCreateUserTask(CreateUserTaskBeforeContext context) {
        log.info("taskName: {}", context.getName());
    }

    @Override
    public void afterCreateUserTask(CreateUserTaskAfterContext context) {
        log.info("taskId: {}", context.getUserTask().getId());
    }
}
