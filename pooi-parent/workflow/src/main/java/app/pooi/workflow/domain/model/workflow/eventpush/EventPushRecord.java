package app.pooi.workflow.domain.model.workflow.eventpush;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventPushRecord {

    private Long id;

    private String eventId;

    private String tenantId;

    private String processDefinitionId;

    private String processInstanceId;

    private String subjectId;

    private String eventType;

    private String event;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
