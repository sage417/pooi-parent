package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.workflow.constant.EventTypeEnum;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventType;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableEntityWithVariablesEvent;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

@Slf4j
@Setter
public class WorkflowFlowableEngineEventListener extends AbstractFlowableEngineEventListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StrongUuidGenerator uuidGenerator = new StrongUuidGenerator();

    @SneakyThrows
    @Override
    protected void activityStarted(FlowableActivityEvent event) {
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findById(event.getExecutionId());

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> {
            log.info("procInstId: {} {}({}) {}", event.getProcessInstanceId(), event.getActivityId(), event.getActivityType(), event.getType());
            return new EventRecordDO().setEventId(uuidGenerator.getNextId())
                    .setEventType(EventTypeEnum.ACTIVITY_STARTED)
                    .setTenantId(executionEntity.getTenantId())
                    .setProcessDefinitionId(event.getProcessDefinitionId())
                    .setProcessInstanceId(event.getProcessInstanceId())
                    .setSubjectId(event.getActivityId())
                    .setEvent(null);
        }));
    }

    @SneakyThrows
    @Override
    protected void activityCompleted(FlowableActivityEvent event) {
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findById(event.getExecutionId());

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> {
            log.info("procInstId: {} {}({}) {}", event.getProcessInstanceId(), event.getActivityId(), event.getActivityType(), event.getType());
            return new EventRecordDO().setEventId(uuidGenerator.getNextId())
                    .setEventType(EventTypeEnum.ACTIVITY_COMPLETED)
                    .setTenantId(executionEntity.getTenantId())
                    .setProcessDefinitionId(event.getProcessDefinitionId())
                    .setProcessInstanceId(event.getProcessInstanceId())
                    .setSubjectId(event.getActivityId())
                    .setEvent(null);
        }));

    }

    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findById(event.getExecutionId());

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> {
            TaskEntity taskEntity = ((TaskEntity) event.getEntity());

            log.info("procInstId: {} {}({}) {}", event.getProcessInstanceId(), taskEntity.getTaskDefinitionKey(), taskEntity.getId(), event.getType());

            return new EventRecordDO().setEventId(uuidGenerator.getNextId())
                    .setEventType(EventTypeEnum.USER_TASK_CREATED)
                    .setTenantId(taskEntity.getTenantId())
                    .setProcessDefinitionId(event.getProcessDefinitionId())
                    .setProcessInstanceId(event.getProcessInstanceId())
                    .setSubjectId(taskEntity.getId())
                    .setEvent(taskInfo(taskEntity).toString());
        }));
    }

    @Override
    protected void taskAssigned(FlowableEngineEntityEvent event) {
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findById(event.getExecutionId());

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> {
            TaskEntity taskEntity = ((TaskEntity) event.getEntity());

            log.info("procInstId: {} {}({}) {}", event.getProcessInstanceId(), taskEntity.getTaskDefinitionKey(), taskEntity.getId(), event.getType());

            return new EventRecordDO().setEventId(uuidGenerator.getNextId())
                    .setEventType(EventTypeEnum.USER_TASK_ASSIGNEE)
                    .setTenantId(taskEntity.getTenantId())
                    .setProcessDefinitionId(event.getProcessDefinitionId())
                    .setProcessInstanceId(event.getProcessInstanceId())
                    .setSubjectId(taskEntity.getId())
                    .setEvent(taskInfo(taskEntity).toString());
        }));
    }

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findById(event.getExecutionId());

        if (!(event instanceof FlowableEntityWithVariablesEvent entityWithVariablesEvent)) {
            log.warn("event type:{}", event.getClass().getName());
            return;
        }
        TaskEntity taskEntity = ((TaskEntity) entityWithVariablesEvent.getEntity());
        @SuppressWarnings("rawtypes")
        Map variables = entityWithVariablesEvent.getVariables();
        FlowableEventType eventType = entityWithVariablesEvent.getType();

//        TaskService taskService = CommandContextUtil.getProcessEngineConfiguration().getTaskService();
//        taskService.createTaskQuery().list();

        ObjectNode objectNode = taskInfo(taskEntity);
        objectNode.putPOJO("variables", variables);

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> {
            log.info("procInstId: {} {}({}) {}", event.getProcessInstanceId(), taskEntity.getTaskDefinitionKey(), taskEntity.getId(), event.getType());

            return new EventRecordDO().setEventId(uuidGenerator.getNextId())
                    .setEventType(EventTypeEnum.USER_TASK_COMPLETE)
                    .setTenantId(taskEntity.getTenantId())
                    .setProcessInstanceId(taskEntity.getProcessInstanceId())
                    .setProcessDefinitionId(event.getProcessDefinitionId())
                    .setSubjectId(taskEntity.getId())
                    .setEvent(objectNode.toString());
        }));
    }

    @Override
    protected void processStarted(FlowableProcessStartedEvent event) {

        if (!(event.getEntity() instanceof ExecutionEntity executionEntity)) {
            return;
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        @SuppressWarnings("rawtypes")
        Map variables = event.getVariables();
        objectNode.putPOJO("variables", variables);
        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(executionEntity.getProcessInstanceId(), () -> {
            log.info("procInstId: {} {}({}) {}", executionEntity.getProcessInstanceId(), "", executionEntity.getId(), event.getType());

            return new EventRecordDO().setEventId(uuidGenerator.getNextId())
                    .setEventType(EventTypeEnum.INSTANCE_STARTED)
                    .setTenantId(executionEntity.getTenantId())
                    .setProcessInstanceId(executionEntity.getProcessInstanceId())
                    .setProcessDefinitionId(executionEntity.getProcessDefinitionId())
                    .setSubjectId(executionEntity.getProcessInstanceId())
                    .setEvent(objectNode.toString());
        }));

    }

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        if (!(event.getEntity() instanceof ExecutionEntity executionEntity)) {
            return;
        }
        ObjectNode objectNode = objectMapper.createObjectNode();

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(executionEntity.getProcessInstanceId(), () -> {
            log.info("procInstId: {} {}({}) {}", executionEntity.getProcessInstanceId(), "", executionEntity.getId(), event.getType());

            return new EventRecordDO().setEventId(uuidGenerator.getNextId())
                    .setEventType(EventTypeEnum.INSTANCE_COMPLETED)
                    .setTenantId(executionEntity.getTenantId())
                    .setProcessInstanceId(executionEntity.getProcessInstanceId())
                    .setProcessDefinitionId(executionEntity.getProcessDefinitionId())
                    .setSubjectId(executionEntity.getProcessInstanceId())
                    .setEvent(objectNode.toString());
        }));

    }

    private ObjectNode taskInfo(TaskEntity task) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("id", task.getId());
        objectNode.put("name", task.getName());
        objectNode.put("assignee", task.getAssignee());
        objectNode.put("category", task.getCategory());
        objectNode.put("formKey", task.getFormKey());
        objectNode.put("owner", task.getOwner());
        objectNode.put("parentTaskId", task.getParentTaskId());
        objectNode.put("definitionId", task.getTaskDefinitionId());
        objectNode.put("definitionKey", task.getTaskDefinitionKey());
        return objectNode;
    }
}
