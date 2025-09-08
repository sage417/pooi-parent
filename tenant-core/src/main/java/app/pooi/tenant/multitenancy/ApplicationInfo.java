package app.pooi.tenant.multitenancy;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ApplicationInfo {

    private String applicationCode;

    private String applicationName;

    private Integer applicationStatus;

    private String tenantCode;

    private String tenantName;
}
