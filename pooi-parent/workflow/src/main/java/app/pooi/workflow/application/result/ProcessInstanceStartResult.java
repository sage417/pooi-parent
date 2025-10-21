package app.pooi.workflow.application.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcessInstanceStartResult {

    private String processInstanceId;
}
