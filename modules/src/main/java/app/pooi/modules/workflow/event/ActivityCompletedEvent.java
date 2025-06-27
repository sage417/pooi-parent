package app.pooi.modules.workflow.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName(value = "ACTIVITY_COMPLETED")
@EqualsAndHashCode(callSuper = true)
@Data
public class ActivityCompletedEvent extends WorkFlowEvent.ActivityBaseEvent {
}
