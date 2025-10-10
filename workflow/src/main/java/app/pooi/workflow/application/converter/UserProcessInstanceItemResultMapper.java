package app.pooi.workflow.application.converter;

import app.pooi.workflow.application.result.UserFinishProcessInstanceItemResult;
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
    @Mapping(target = "startTime", source = "startTime")
    UserStartProcessInstanceItemResult convert(HistoricProcessInstance historicProcessInstance);

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "currentNodeName", source = "name")
    UserTodoProcessInstanceItemResult convert(Task task);

    @Mapping(target = "processInstanceName", source = "name")
    @Mapping(target = "startTime", source = "startTime")
    UserTodoProcessInstanceItemResult update(@MappingTarget UserTodoProcessInstanceItemResult itemResult, HistoricProcessInstance instance);

    UserFinishProcessInstanceItemResult convertFinishResult(HistoricProcessInstance historicProcessInstance);

    @Mapping(target = "lastFinishedTaskId", source = "id")
    UserFinishProcessInstanceItemResult updateFinishResult(@MappingTarget UserFinishProcessInstanceItemResult itemResult, HistoricTaskInstance historicTaskInstance);
}
