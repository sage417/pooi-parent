package app.pooi.workflow.infrastructure.persistence.entity.workflow.procquery;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserCompletedProcessTaskQuery {

    private String userId;

    private String applicationCode;
}
