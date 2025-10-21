package app.pooi.workflow.infrastructure.persistence.service.workflow.comment;

import app.pooi.workflow.infrastructure.persistence.entity.workflow.comment.CommentEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentEntityService extends IService<CommentEntity> {

    List<CommentEntity> listByInstanceId(String processInstanceId);
}
