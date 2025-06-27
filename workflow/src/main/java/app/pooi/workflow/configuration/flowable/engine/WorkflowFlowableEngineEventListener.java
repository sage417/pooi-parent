package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.modules.workflow.event.*;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;

@Slf4j
@Setter
public class WorkflowFlowableEngineEventListener extends AbstractFlowableEngineEventListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private EventMapper eventMapper;

    @SneakyThrows
    @Override
    protected void activityStarted(FlowableActivityEvent event) {
        HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getHistoricProcessInstanceEntityManager()
                .findById(event.getProcessInstanceId());
        ActivityStartedEvent activityStartedEvent = eventMapper.activityStartEvent(historicProcessInstance);
        activityStartedEvent.setActivityId(event.getActivityId());

        EventRecordDO eventRecordDO = eventMapper.activityStartedEventRecordDO(event);
        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                .setEvent(activityStartedEvent);
        eventRecordDO.setEvent(objectMapper.writeValueAsString(eventPayload));

        TransactionSynchronizationManager.registerSynchronization(
                new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> eventRecordDO));
    }

    @SneakyThrows
    @Override
    protected void activityCompleted(FlowableActivityEvent event) {
        HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getHistoricProcessInstanceEntityManager()
                .findById(event.getProcessInstanceId());
        ActivityCompletedEvent activityCompletedEvent = eventMapper.activityEndEvent(historicProcessInstance);
        activityCompletedEvent.setActivityId(event.getActivityId());

        EventRecordDO eventRecordDO = eventMapper.activityCompletedEventRecordDO(event);
        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                .setEvent(activityCompletedEvent);
        eventRecordDO.setEvent(objectMapper.writeValueAsString(eventPayload));

        TransactionSynchronizationManager.registerSynchronization(
                new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> eventRecordDO));
    }

    @Override
    @SneakyThrows
    protected void taskCreated(FlowableEngineEntityEvent event) {
        HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getHistoricProcessInstanceEntityManager()
                .findById(event.getProcessInstanceId());

        EventRecordDO eventRecordDO = eventMapper.taskCreatedEventRecordDO((TaskEntity) event.getEntity());
        UserTaskCreatedEvent userTaskCreatedEvent = eventMapper.taskCreatedEvent(historicProcessInstance, ((TaskEntity) event.getEntity()));

        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                .setEvent(userTaskCreatedEvent);
        eventRecordDO.setEvent(objectMapper.writeValueAsString(eventPayload));

        TransactionSynchronizationManager.registerSynchronization(
                new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> eventRecordDO));
    }

    @Override
    @SneakyThrows
    protected void taskAssigned(FlowableEngineEntityEvent event) {

        HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getHistoricProcessInstanceEntityManager()
                .findById(event.getProcessInstanceId());

        EventRecordDO eventRecordDO = eventMapper.taskAssigneeEventRecordDO((TaskEntity) event.getEntity());
        UserTaskAssigneeEvent userTaskAssigneeEvent = eventMapper.taskAssigneeEvent(historicProcessInstance, ((TaskEntity) event.getEntity()));

        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                .setEvent(userTaskAssigneeEvent);
        eventRecordDO.setEvent(objectMapper.writeValueAsString(eventPayload));

        TransactionSynchronizationManager.registerSynchronization(
                new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> eventRecordDO));
    }

    @Override
    @SneakyThrows
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getHistoricProcessInstanceEntityManager()
                .findById(event.getProcessInstanceId());

        EventRecordDO eventRecordDO = eventMapper.taskCompletedEventRecordDO((TaskEntity) event.getEntity());
        UserTaskCompletedEvent userTaskCompletedEvent = eventMapper.taskCompletedEvent(historicProcessInstance, ((TaskEntity) event.getEntity()));

        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                .setEvent(userTaskCompletedEvent);
        eventRecordDO.setEvent(objectMapper.writeValueAsString(eventPayload));

        TransactionSynchronizationManager.registerSynchronization(
                new EventListenerTransactionSynchronization(event.getProcessInstanceId(), () -> eventRecordDO));
    }

    @Override
    @SneakyThrows
    protected void processStarted(FlowableProcessStartedEvent event) {
        if (!(event.getEntity() instanceof ExecutionEntity executionEntity)) {
            return;
        }

        EventRecordDO eventRecordDO = this.eventMapper.processInstanceStartRecordDO(executionEntity);
        InstanceStartedEvent instanceStartedEvent = this.eventMapper.instanceStartedEvent(executionEntity);

        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                .setEvent(instanceStartedEvent);
        eventRecordDO.setEvent(this.objectMapper.writeValueAsString(eventPayload));

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(executionEntity.getProcessInstanceId(), () -> eventRecordDO));
    }

    @Override
    @SneakyThrows
    protected void processCompleted(FlowableEngineEntityEvent event) {
        if (!(event.getEntity() instanceof ExecutionEntity executionEntity)) {
            return;
        }

        EventRecordDO eventRecordDO = this.eventMapper.processInstanceCompleteRecordDO(executionEntity);
        InstanceCompletedEvent instanceCompletedEvent = this.eventMapper.instanceCompletedEvent(executionEntity);

        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header(eventRecordDO.getEventId(), eventRecordDO.getEventType()))
                .setEvent(instanceCompletedEvent);
        eventRecordDO.setEvent(this.objectMapper.writeValueAsString(eventPayload));

        TransactionSynchronizationManager.registerSynchronization(new EventListenerTransactionSynchronization(executionEntity.getProcessInstanceId(), () -> eventRecordDO));
    }

}
