package app.pooi.workflow.infrastructure.messaging.event;

import app.pooi.workflow.domain.event.WorkFlowEvent;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EventPayload {
    /**
     *
     */
    private Header header;
    /**
     *
     */
    private WorkFlowEvent event;
}
