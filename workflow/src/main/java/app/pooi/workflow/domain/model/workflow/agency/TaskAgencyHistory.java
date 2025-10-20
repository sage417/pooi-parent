package app.pooi.workflow.domain.model.workflow.agency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TaskAgencyHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
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
     * 任务节点id
     */
    private String taskDefinitionKey;

    /**
     * 委托类型 0:无效 1:代理 2:共享
     */
    private Integer type;

    /**
     * 委托详情
     */
    private TaskApprovalNode delegateDetails;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

}
