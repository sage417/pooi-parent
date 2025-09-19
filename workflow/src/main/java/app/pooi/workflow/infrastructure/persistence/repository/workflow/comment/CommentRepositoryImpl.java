package app.pooi.workflow.infrastructure.persistence.repository.workflow.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.repository.CommentRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.comment.CommentConverter;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.comment.CommentEntity;
import app.pooi.workflow.infrastructure.persistence.service.workflow.comment.CommentEntityService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Repository
class CommentRepositoryImpl implements CommentRepository {

    @Resource
    private CommentEntityService commentEntityService;

    @Resource
    private CommentConverter converter;

    @Override
    public boolean save(Comment comment) {
        return commentEntityService.save(converter.toEntity(comment));
    }

    @Override
    public List<Comment> listByInstanceId(String processInstanceId) {
        List<CommentEntity> commentEntities = commentEntityService.listByInstanceId(processInstanceId);
        return commentEntities.stream().map(converter::toModel).collect(Collectors.toList());
    }
}
