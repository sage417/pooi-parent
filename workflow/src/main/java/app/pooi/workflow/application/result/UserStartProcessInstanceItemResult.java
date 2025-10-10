package app.pooi.workflow.application.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserStartProcessInstanceItemResult {

    private String processInstanceId;

    private String processInstanceName;

    private LocalDateTime startTime;

    private String currentNodeName;

    private String currentTaskCandidates;

}