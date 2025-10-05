package app.pooi.workflow.domain.service.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.repository.CommentRepository;
import lombok.NonNull;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CommentService {

    @Resource
    private CommentRepository commentRepository;

    @Resource
    private CommentMapper commentConvert;

    public Comment createFromTask(@NonNull Task task, @NonNull String type) {
        Comment comment = new Comment(type);
        commentConvert.updateFromTask(comment, task);
        return comment;
    }

    public Comment createFormExecution(@NonNull ExecutionEntity execution) {
        return commentConvert.convertFromExecution(execution);
    }

    public Comment createFromInstance(@NonNull ProcessInstance instance) {
        Comment comment = new Comment("INSTANCE_START");
        return commentConvert.updateFromInstance(comment, instance);
    }

    public boolean recordComment(@NonNull Comment comment) {
        return commentRepository.save(comment, true);
    }

    public boolean cacheComment(@NonNull Comment comment) {
        return commentRepository.save(comment, false);
    }

    public List<Comment> listByInstanceId(@NonNull String processInstanceId) {
        return commentRepository.listByInstanceId(processInstanceId);
    }
}
