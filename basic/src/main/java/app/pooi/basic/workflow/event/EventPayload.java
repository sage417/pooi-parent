package app.pooi.basic.workflow.event;

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
