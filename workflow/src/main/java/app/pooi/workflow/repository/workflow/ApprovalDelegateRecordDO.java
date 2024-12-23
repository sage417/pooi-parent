package app.pooi.workflow.repository.workflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Accessors(chain = true)
@Data
@TableName("t_workflow_approval_delegate_record")
public class ApprovalDelegateRecordDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户标识
     */
    private String tenantId;

    /**
     * 流程定义id
     */
    private String processDefinitionKey;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 委托类型 0:无效 1:全权委托 2:协助审批
     */
    private Integer type;

    /**
     * 委托人
     */
    private String delegate;

    /**
     * 代理人
     */
    private String agents;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;


}
