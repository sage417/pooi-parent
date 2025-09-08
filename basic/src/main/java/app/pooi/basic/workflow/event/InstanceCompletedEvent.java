package app.pooi.basic.workflow.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName(value = "INSTANCE_COMPLETED")
@EqualsAndHashCode(callSuper = true)
@Data
public class InstanceCompletedEvent extends WorkFlowEvent.InstanceBaseEvent {


}
