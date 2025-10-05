package app.pooi.workflow.application.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
public class ProcessTimelineItemResult {

    private final String id;

    private String processInstanceId;

    private String nodeId;

    private String nodeName;

    private String taskId;

    private String operationType;

    private String operationDetail;

    private String operator;

    private String operatorName;

    private Set<String> assigneeAndCandidates;

    private LocalDateTime operationTime;
}
