package app.pooi.workflow.repository.tenant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("t_tenant_info")
@Data
public class TenantInfoDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String tenantName;

    private String tenantCode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
