package app.pooi.modules.workflow.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName(value = "INSTANCE_STARTED")
@EqualsAndHashCode(callSuper = true)
@Data
public class InstanceStartedEvent extends WorkFlowEvent.InstanceBaseEvent {


}
