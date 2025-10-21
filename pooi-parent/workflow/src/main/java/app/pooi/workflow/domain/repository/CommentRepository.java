package app.pooi.workflow.domain.repository;

import app.pooi.workflow.domain.model.workflow.comment.Comment;

import java.util.List;

public interface CommentRepository {

    boolean save(Comment entity, boolean flushCache);

    List<Comment> listByInstanceId(String processInstanceId);
}
