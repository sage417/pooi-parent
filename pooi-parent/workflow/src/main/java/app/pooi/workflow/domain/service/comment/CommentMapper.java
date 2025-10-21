package app.pooi.workflow.domain.service.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.util.SecurityContextUtil;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", imports = {SecurityContextUtil.class})
interface CommentMapper {

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "nodeId", source = "task.taskDefinitionKey")
    @Mapping(target = "operationDetail", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Comment updateFromTask(@MappingTarget Comment comment, Task task);

    @Mapping(target = "taskId", constant = "")
    @Mapping(target = "nodeId", source = "activityId")
    @Mapping(target = "operationDetail", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Comment convertFromExecution(ExecutionEntity execution);

    @Mapping(target = "taskId", constant = "")
    @Mapping(target = "nodeId", constant = "")
    @Mapping(target = "operationDetail", ignore = true)
    @Mapping(target = "operatorAccount", source = "instance.startUserId")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Comment updateFromInstance(@MappingTarget Comment comment, ProcessInstance instance);
}
