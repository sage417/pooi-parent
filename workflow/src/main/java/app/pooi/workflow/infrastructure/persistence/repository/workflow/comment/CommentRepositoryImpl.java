package app.pooi.workflow.infrastructure.persistence.repository.workflow.comment;

import app.pooi.workflow.domain.model.workflow.comment.Comment;
import app.pooi.workflow.domain.repository.CommentRepository;
import app.pooi.workflow.infrastructure.persistence.converter.workflow.comment.CommentConverter;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.comment.CommentEntity;
import app.pooi.workflow.infrastructure.persistence.service.workflow.comment.CommentEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
class CommentRepositoryImpl implements CommentRepository {

    private static final ThreadLocal<Deque<CommentEntity>> TRANSACTIONAL_COMMENT_ENTITY_HOLDER = ThreadLocal.withInitial(ArrayDeque::new);

    @Resource
    private CommentEntityService commentEntityService;

    @Resource
    private CommentConverter converter;

    @Override
    public boolean save(Comment comment, boolean flushCache) {
        Deque<CommentEntity> deque = TRANSACTIONAL_COMMENT_ENTITY_HOLDER.get();
        CommentEntity commentEntity = converter.toEntity(comment);

        if (flushCache) {
            commentEntityService.save(commentEntity);
            deque.forEach(commentEntityService::save);
            TRANSACTIONAL_COMMENT_ENTITY_HOLDER.remove();
        } else {
            // cache first time
            if (deque.isEmpty()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void beforeCommit(boolean readOnly) {
                        Deque<CommentEntity> deque = TRANSACTIONAL_COMMENT_ENTITY_HOLDER.get();
                        try {
                            if (!readOnly && !deque.isEmpty()) {
                                // FALL BACK didnt flush before commit
                                log.warn("not flush cache before commit");
                                deque.forEach(commentEntityService::save);
                            }
                        } finally {
                            TRANSACTIONAL_COMMENT_ENTITY_HOLDER.remove();
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        TRANSACTIONAL_COMMENT_ENTITY_HOLDER.remove();
                    }
                });
            }
            deque.push(commentEntity);
        }
        return true;
    }

    @Override
    public List<Comment> listByInstanceId(String processInstanceId) {
        List<CommentEntity> commentEntities = commentEntityService.listByInstanceId(processInstanceId);
        return commentEntities.stream().map(converter::toModel).collect(Collectors.toList());
    }
}
