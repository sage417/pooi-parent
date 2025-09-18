package app.pooi.workflow.infrastructure.persistence.converter.workflow.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.comment.CommentEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CommentConverter {

    CommentEntity toEntity(final Comment comment);

    Comment toModel(CommentEntity entity);
}
