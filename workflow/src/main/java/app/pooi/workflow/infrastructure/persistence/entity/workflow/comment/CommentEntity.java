package app.pooi.workflow.infrastructure.persistence.entity.workflow.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
@TableName("t_workflow_comment")
public class CommentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tenantId;

    private String processDefinitionId;

    private String processInstanceId;

    private String nodeId;

    private String taskId;

    private String type;

    private String operatorAccount;

    private String operationDetail;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
