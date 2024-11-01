package app.pooi.workflow.repository.domain.workflow;

import app.pooi.workflow.repository.workflow.CommentDO;
import app.pooi.workflow.repository.workflow.CommentRepository;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl extends ServiceImpl<CommentMapper, CommentDO> implements CommentRepository {

    @Override
    public List<CommentDO> listByInstanceId(String processInstanceId) {
        return getBaseMapper().selectList(Wrappers.lambdaQuery(CommentDO.class)
                .eq(CommentDO::getProcessInstanceId, processInstanceId));
    }
}
