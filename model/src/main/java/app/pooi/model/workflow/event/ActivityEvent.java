package app.pooi.model.workflow.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

public interface ActivityEvent extends WorkFlowEvent {
    /**
     *
     * @return
     */
    String getActivityId();

    @Accessors(chain = true)
    @Data
    abstract class AbstractActivityEvent implements ActivityEvent, Serializable {

        private String processInstanceId;

        private String processDefinitionId;

        private String processDefinitionKey;

        private String processDefinitionVersion;

        private String activityId;


    }

    @JsonTypeName(value = "ACTIVITY_COMPLETED")
    @EqualsAndHashCode(callSuper = true)
    @Data
    class ActivityCompletedEvent extends AbstractActivityEvent {

    }

    @JsonTypeName(value = "ACTIVITY_STARTED")
    @EqualsAndHashCode(callSuper = true)
    @Data
    class ActivityStartedEvent extends AbstractActivityEvent {

    }
}
