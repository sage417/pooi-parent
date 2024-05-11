package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.workflow.constant.EventTypeEnum;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import app.pooi.workflow.repository.workflow.EventRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableEntityWithVariablesEvent;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Setter
public class WorkflowFlowableEngineEventListener extends AbstractFlowableEngineEventListener {

    @Resource
    private EventRecordRepository eventRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @SneakyThrows
    @Override
    protected void activityStarted(FlowableActivityEvent event) {
        log.info("{}({}) {}", event.getActivityId(), event.getActivityType(), event.getType());
    }

    @SneakyThrows
    @Override
    protected void activityCompleted(FlowableActivityEvent event) {
        log.info("{}({}) {}", event.getActivityId(), event.getActivityType(), event.getType());
    }

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        if (!(event instanceof FlowableEntityWithVariablesEvent)) {
            log.warn("event type:{}", event.getClass().getName());
            return;
        }
        FlowableEntityWithVariablesEvent entityWithVariablesEvent = (FlowableEntityWithVariablesEvent) event;
        TaskEntity taskEntity = ((TaskEntity) entityWithVariablesEvent.getEntity());
        Map<String, Object> variables = entityWithVariablesEvent.getVariables();
        FlowableEventType eventType = entityWithVariablesEvent.getType();

//        TaskService taskService = CommandContextUtil.getProcessEngineConfiguration().getTaskService();
//        taskService.createTaskQuery().list();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("variables", variables);

        TransactionSynchronizationManager.registerSynchronization(new SerialTransactionSynchronization(taskEntity, variables, () -> {
            eventRecordRepository.save(new EventRecordDO().setEventId("")
                    .setEventType(EventTypeEnum.USER_TASK_COMPLETE)
                    .setTenantId(taskEntity.getTenantId())
                    .setProcessInstanceId(taskEntity.getProcessInstanceId())
                    .setSubjectId(taskEntity.getId())
                    .setEvent(objectNode.toString()));
        }));

        super.taskCompleted(event);
    }

    private static class SerialTransactionSynchronization implements TransactionSynchronization {
        private final TaskEntity taskEntity;

        private final ImmutableMap<String, Object> variables;

        private static ThreadLocal<List<Runnable>> TASKS = ThreadLocal.withInitial(ArrayList::new);

        public SerialTransactionSynchronization(TaskEntity taskEntity, Map<String, Object> variables, Runnable runnableTask) {
            this.taskEntity = taskEntity;
            this.variables = ImmutableMap.copyOf(variables);
            TASKS.get().add(runnableTask);
        }

        @Override
        public void afterCommit() {
            if (TASKS.get().isEmpty()) {
                return;
            }
            TASKS.get().forEach(Runnable::run);
            TASKS.get().clear();
        }

        @Override
        public void afterCompletion(int status) {
            TASKS.get().clear();
        }
    }
}
