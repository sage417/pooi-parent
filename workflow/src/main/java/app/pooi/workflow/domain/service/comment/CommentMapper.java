package app.pooi.workflow.domain.service.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface CommentMapper {

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "nodeId", source = "task.taskDefinitionKey")
    Comment updateFromTask(@MappingTarget Comment comment, Task task);

    @Mapping(target = "taskId", constant = "")
    @Mapping(target = "nodeId", source = "activityId")
    Comment convert2DO(Execution execution);
}
