package app.pooi.workflow.repository.workflow;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentRepository extends IService<CommentDO> {

    List<CommentDO> listByInstanceId(String processInstanceId);
}
