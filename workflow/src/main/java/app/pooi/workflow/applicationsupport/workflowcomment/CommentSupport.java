package app.pooi.workflow.applicationsupport.workflowcomment;

import app.pooi.workflow.repository.workflow.CommentDO;
import app.pooi.workflow.repository.workflow.CommentRepository;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class CommentSupport {

    @Resource
    private CommentRepository commentRepository;

    @Resource
    private CommentConvert commentConvert;

    public AddCommentBO createFromTask(Task task) {
        AddCommentBO addCommentBO = new AddCommentBO();
        commentConvert.updateFromTask(addCommentBO, task);
        return addCommentBO;
    }


    public void recordComment(AddCommentBO addCommentBO) {
        CommentDO commentDO = commentConvert.convert2DO(addCommentBO);
        boolean saved = commentRepository.save(commentDO);
    }

    public List<CommentDO> listByInstanceId(String processInstanceId) {
        return commentRepository.listByInstanceId(processInstanceId);
    }
}
