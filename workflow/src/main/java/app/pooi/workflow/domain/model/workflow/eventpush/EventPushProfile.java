package app.pooi.workflow.domain.model.workflow.eventpush;

import app.pooi.workflow.domain.model.enums.EventPushType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventPushProfile {

    private Long id;

    private String tenantId;

    private EventPushType type;

    private String profile;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
