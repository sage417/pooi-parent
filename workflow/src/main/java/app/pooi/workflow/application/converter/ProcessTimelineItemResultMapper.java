package app.pooi.workflow.application.converter;

import app.pooi.workflow.application.result.ProcessTimelineItemResult;
import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.util.TaskEntityUtil;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.TaskInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring", imports = {TaskEntityUtil.class})
public interface ProcessTimelineItemResultMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "processInstanceId", source = "processInstanceId")
    @Mapping(target = "nodeId", source = "nodeId")
    @Mapping(target = "nodeName", ignore = true)
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "operationType", source = "type")
    @Mapping(target = "operationDetail", source = "operationDetail")
    @Mapping(target = "operator", source = "operatorAccount")
    @Mapping(target = "operatorName", ignore = true)
    @Mapping(target = "assigneeAndCandidates", ignore = true)
    @Mapping(target = "operationTime", source = "createTime")
    ProcessTimelineItemResult convert(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "processInstanceId", source = "processInstanceId")
    @Mapping(target = "nodeId", source = "taskDefinitionKey")
    @Mapping(target = "nodeName", ignore = true)
    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "operationType", constant = "APPROVAL")
    @Mapping(target = "operationDetail", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "operatorName", ignore = true)
    @Mapping(target = "assigneeAndCandidates", expression = "java(TaskEntityUtil.getAssigneeAndCandidates(task))")
    @Mapping(target = "operationTime", ignore = true)
    ProcessTimelineItemResult convert(TaskInfo task);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "processInstanceId", ignore = true)
    @Mapping(target = "nodeId", source = "userTask.id")
    @Mapping(target = "nodeName", source = "userTask.name")
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "operationType", constant = "")
    @Mapping(target = "operationDetail", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "operatorName", ignore = true)
    @Mapping(target = "assigneeAndCandidates", source = "candidates")
    @Mapping(target = "operationTime", ignore = true)
    ProcessTimelineItemResult convert(UserTask userTask, Set<String> candidates);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "processInstanceId", ignore = true)
    @Mapping(target = "nodeId", source = "id")
    @Mapping(target = "nodeName", source = "name")
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "operationType", constant = "START")
    @Mapping(target = "operationDetail", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "operatorName", ignore = true)
    @Mapping(target = "assigneeAndCandidates", ignore = true)
    @Mapping(target = "operationTime", ignore = true)
    ProcessTimelineItemResult convert(StartEvent startEvent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "processInstanceId", ignore = true)
    @Mapping(target = "nodeId", source = "id")
    @Mapping(target = "nodeName", source = "name")
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "operationType", constant = "END")
    @Mapping(target = "operationDetail", ignore = true)
    @Mapping(target = "operator", ignore = true)
    @Mapping(target = "operatorName", ignore = true)
    @Mapping(target = "assigneeAndCandidates", ignore = true)
    @Mapping(target = "operationTime", ignore = true)
    ProcessTimelineItemResult convert(EndEvent endEvent);
}
