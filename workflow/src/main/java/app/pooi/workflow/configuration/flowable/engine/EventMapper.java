package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.basic.workflow.event.*;
import app.pooi.tenant.multitenancy.ApplicationInfoHolder;
import app.pooi.workflow.repository.workflow.EventRecordDO;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.annotation.Resource;

@Mapper(componentModel = "spring")
public abstract class EventMapper {
    @Resource
    protected ApplicationInfoHolder applicationInfoHolder;

    protected final StrongUuidGenerator uuidGenerator = new StrongUuidGenerator();


    // ---------------------------        activityEvent          ---------------------------------------//
    @SuppressWarnings("unused")
    @Mapping(target = "processInstanceId", source = "id")
    @Mapping(target = "processDefinitionId", source = "processDefinitionId")
    @Mapping(target = "processDefinitionKey", source = "processDefinitionKey")
    @Mapping(target = "processDefinitionVersion", source = "processDefinitionVersion")
    abstract WorkFlowEvent.ActivityBaseEvent activityBaseEvent(HistoricProcessInstance processInstance);

    @InheritConfiguration(name = "activityBaseEvent")
    abstract ActivityStartedEvent activityStartEvent(HistoricProcessInstance processInstance);

    @InheritConfiguration(name = "activityBaseEvent")
    abstract ActivityCompletedEvent activityEndEvent(HistoricProcessInstance processInstance);

    @SuppressWarnings("unused")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", expression = "java(applicationInfoHolder.getApplicationCode())")
    @Mapping(target = "eventId", expression = "java(uuidGenerator.getNextId())")
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "processDefinitionId", source = "processDefinitionId")
    @Mapping(target = "processInstanceId", source = "processInstanceId")
    @Mapping(target = "subjectId", source = "activityId")
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    abstract EventRecordDO activityEventRecordDO(FlowableActivityEvent event);

    @InheritConfiguration(name = "activityEventRecordDO")
    @Mapping(target = "eventType", expression = "java(EventTypeEnum.ACTIVITY_STARTED.getValue())")
    abstract EventRecordDO activityStartedEventRecordDO(FlowableActivityEvent event);

    @InheritConfiguration(name = "activityEventRecordDO")
    @Mapping(target = "eventType", expression = "java(EventTypeEnum.ACTIVITY_COMPLETED.getValue())")
    abstract EventRecordDO activityCompletedEventRecordDO(FlowableActivityEvent event);

    // ---------------------------        activityEvent          ---------------------------------------//

    // ---------------------------        taskEvent          ---------------------------------------//

    @SuppressWarnings("unused")
    @Mapping(target = "taskId", source = "taskEntity.id")
    @Mapping(target = "processInstanceId", source = "taskEntity.processInstanceId")
    @Mapping(target = "processDefinitionId", source = "taskEntity.processDefinitionId")
    @Mapping(target = "processDefinitionKey", source = "processInstance.processDefinitionKey")
    @Mapping(target = "processDefinitionVersion", source = "processInstance.processDefinitionVersion")
    abstract WorkFlowEvent.TaskBaseEvent taskBaseEvent(HistoricProcessInstance processInstance, TaskEntity taskEntity);

    @InheritConfiguration(name = "taskBaseEvent")
    abstract UserTaskCreatedEvent taskCreatedEvent(HistoricProcessInstance processInstance, TaskEntity taskEntity);

    @InheritConfiguration(name = "taskBaseEvent")
    abstract UserTaskAssigneeEvent taskAssigneeEvent(HistoricProcessInstance processInstance, TaskEntity taskEntity);

    @InheritConfiguration(name = "taskBaseEvent")
    abstract UserTaskCompletedEvent taskCompletedEvent(HistoricProcessInstance processInstance, TaskEntity taskEntity);

    @SuppressWarnings("unused")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", expression = "java(applicationInfoHolder.getApplicationCode())")
    @Mapping(target = "eventId", expression = "java(uuidGenerator.getNextId())")
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "processDefinitionId", source = "processDefinitionId")
    @Mapping(target = "processInstanceId", source = "processInstanceId")
    @Mapping(target = "subjectId", source = "id")
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    abstract EventRecordDO taskEventRecordDO(TaskEntity taskEntity);

    @InheritConfiguration(name = "taskEventRecordDO")
    @Mapping(target = "eventType", expression = "java(EventTypeEnum.USER_TASK_CREATED.getValue())")
    abstract EventRecordDO taskCreatedEventRecordDO(TaskEntity event);

    @InheritConfiguration(name = "taskEventRecordDO")
    @Mapping(target = "eventType", expression = "java(EventTypeEnum.USER_TASK_ASSIGNEE.getValue())")
    abstract EventRecordDO taskAssigneeEventRecordDO(TaskEntity event);

    @InheritConfiguration(name = "taskEventRecordDO")
    @Mapping(target = "eventType", expression = "java(EventTypeEnum.USER_TASK_COMPLETE.getValue())")
    abstract EventRecordDO taskCompletedEventRecordDO(TaskEntity event);

    // ---------------------------        taskEvent          ---------------------------------------//


    // ---------------------------        instanceEvent          ---------------------------------------//
    @Mapping(target = "processInstanceId", source = "id")
    @Mapping(target = "processDefinitionId", source = "processDefinitionId")
    @Mapping(target = "processDefinitionKey", source = "processDefinitionKey")
    @Mapping(target = "processDefinitionVersion", source = "processDefinitionVersion")
    abstract InstanceStartedEvent instanceStartedEvent(ExecutionEntity executionEntity);

    @Mapping(target = "processInstanceId", source = "id")
    @Mapping(target = "processDefinitionId", source = "processDefinitionId")
    @Mapping(target = "processDefinitionKey", source = "processDefinitionKey")
    @Mapping(target = "processDefinitionVersion", source = "processDefinitionVersion")
    abstract InstanceCompletedEvent instanceCompletedEvent(ExecutionEntity executionEntity);

    @SuppressWarnings("unused")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", expression = "java(applicationInfoHolder.getApplicationCode())")
    @Mapping(target = "eventId", expression = "java(uuidGenerator.getNextId())")
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "processDefinitionId", source = "executionEntity.processDefinitionId")
    @Mapping(target = "processInstanceId", source = "executionEntity.processInstanceId")
    @Mapping(target = "subjectId", source = "executionEntity.processInstanceId")
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    abstract EventRecordDO processInstanceRecordDO(ExecutionEntity executionEntity);

    @InheritConfiguration(name = "processInstanceRecordDO")
    @Mapping(target = "eventType", expression = "java(EventTypeEnum.INSTANCE_STARTED.getValue())")
    abstract EventRecordDO processInstanceStartRecordDO(ExecutionEntity executionEntity);


    @InheritConfiguration(name = "processInstanceRecordDO")
    @Mapping(target = "eventType", expression = "java(EventTypeEnum.INSTANCE_COMPLETED.getValue())")
    abstract EventRecordDO processInstanceCompleteRecordDO(ExecutionEntity executionEntity);
    // ---------------------------        instanceEvent          ---------------------------------------//
}
