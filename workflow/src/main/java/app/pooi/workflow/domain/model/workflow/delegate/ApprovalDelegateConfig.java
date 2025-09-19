/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.domain.model.workflow.delegate;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApprovalDelegateConfig implements Serializable {

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
//    @TableField(typeHandler = )
    private List<String> agents;

    /**
     * 生效时间
     */
    private LocalDateTime validTime;

    /**
     * 失效时间
     */
    private LocalDateTime invalidTime;

    /**
     * create_time
     */
    private LocalDateTime createTime;

    /**
     * update_time
     */
    private LocalDateTime updateTime;

    /**
     * is_delete
     */
    private Integer isDelete;
}
