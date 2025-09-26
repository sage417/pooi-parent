package app.pooi.workflow.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName(value = "ACTIVITY_STARTED")
@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityStartedEvent extends WorkFlowEvent.ActivityBaseEvent {
}
