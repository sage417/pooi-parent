package app.pooi.workflow.domain.service.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.repository.CommentRepository;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
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

    public Comment createFromTask(Task task, String type) {
        Comment comment = new Comment(type);
        commentConvert.updateFromTask(comment, task);
        return comment;
    }

    public Comment createFormExecution(ExecutionEntity execution) {
        return commentConvert.convertFromExecution(execution);
    }

    public boolean recordComment(Comment comment) {
        return commentRepository.save(comment, true);
    }

    public boolean cacheComment(Comment comment) {
        return commentRepository.save(comment, false);
    }

    public List<Comment> listByInstanceId(String processInstanceId) {
        return commentRepository.listByInstanceId(processInstanceId);
    }
}
