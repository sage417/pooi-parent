package app.pooi.workflow.domain.model.workflow.eventpush;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventPushProfile {

    private Long id;

    private String tenantId;

    private String type;

    private String profile;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
