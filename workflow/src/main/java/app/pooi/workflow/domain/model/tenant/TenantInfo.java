package app.pooi.workflow.domain.model.tenant;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantInfo {

    private Long id;

    private String tenantName;

    private String tenantCode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
