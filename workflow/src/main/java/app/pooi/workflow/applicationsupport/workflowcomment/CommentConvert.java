package app.pooi.workflow.applicationsupport.workflowcomment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentConvert {


    Comment convert2DO(AddCommentBO bo);

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "nodeId", source = "taskDefinitionId")
    AddCommentBO updateFromTask(@MappingTarget AddCommentBO bo, Task task);

    @Mapping(target = "taskId", constant = "")
    @Mapping(target = "nodeId", source = "activityId")
    AddCommentBO convert2DO(Execution execution);
}
