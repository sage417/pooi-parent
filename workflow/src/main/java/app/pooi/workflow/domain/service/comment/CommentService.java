package app.pooi.workflow.domain.service.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.repository.CommentRepository;
import org.flowable.engine.runtime.Execution;
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

    public Comment createFormExecution(Execution execution) {
        return commentConvert.convert2DO(execution);
    }

    public boolean recordComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> listByInstanceId(String processInstanceId) {
        return commentRepository.listByInstanceId(processInstanceId);
    }
}
