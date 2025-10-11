package app.pooi.workflow.application.converter;

import app.pooi.workflow.application.result.UserFinishedProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserStartProcessInstanceItemResult;
import app.pooi.workflow.application.result.UserTodoProcessInstanceItemResult;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserProcessInstanceItemResultMapper {

    @Mapping(target = "processInstanceId", source = "id")
    @Mapping(target = "processInstanceName", source = "name")
    @Mapping(target = "processInstanceStartTime", source = "startTime")
    UserStartProcessInstanceItemResult convertStartResult(HistoricProcessInstance historicProcessInstance);

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "currentNodeNames", source = "name")
    UserTodoProcessInstanceItemResult convertTodoResult(Task task);

    @Mapping(target = "processInstanceName", source = "name")
    @Mapping(target = "processInstanceStartTime", source = "startTime")
    UserTodoProcessInstanceItemResult updateTodoResult(@MappingTarget UserTodoProcessInstanceItemResult itemResult, HistoricProcessInstance instance);

    @Mapping(target = "processInstanceId", source = "id")
    @Mapping(target = "processInstanceName", source = "name")
    @Mapping(target = "processInstanceStartTime", source = "startTime")
    UserFinishedProcessInstanceItemResult convertFinishResult(HistoricProcessInstance historicProcessInstance);

    @Mapping(target = "lastFinishedTaskId", source = "id")
    @Mapping(target = "lastFinishedTaskTime", source = "endTime")
    UserFinishedProcessInstanceItemResult updateFinishResult(@MappingTarget UserFinishedProcessInstanceItemResult itemResult, HistoricTaskInstance historicTaskInstance);


}
