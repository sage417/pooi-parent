package app.pooi.workflow.infrastructure.persistence.entity.workflow.delegate;

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
@TableName("t_workflow_task_agency_history")
public class TaskAgencyHistoryEntity implements Serializable {

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
     * 任务节点id
     */
    private String taskDefinitionKey;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 委托类型 0:无效 1:代理 2:共享
     */
    private Integer type;

    /**
     * 委托人
     */
    private String delegateDetails;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;


}
