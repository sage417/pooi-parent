package app.pooi.workflow.domain.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessInstanceStartResult {

    private String processInstanceId;
}
