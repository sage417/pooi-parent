package app.pooi.workflow.infrastructure.persistence.service.impl.workflow.comment;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.comment.CommentEntity;
import app.pooi.workflow.infrastructure.persistence.mapper.workflow.comment.CommentEntityMapper;
import app.pooi.workflow.infrastructure.persistence.service.workflow.comment.CommentEntityService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentEntityServiceImpl extends ServiceImpl<CommentEntityMapper, CommentEntity> implements CommentEntityService {

    @Override
    public List<CommentEntity> listByInstanceId(String processInstanceId) {
        return getBaseMapper().selectList(Wrappers.lambdaQuery(CommentEntity.class)
                .eq(CommentEntity::getProcessInstanceId, processInstanceId));
    }
}
