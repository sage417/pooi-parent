package app.pooi.model.workflow.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType", visible = false, include = JsonTypeInfo.As.PROPERTY)
public interface WorkFlowEvent {

    /**
     * process definition
     */
    String getProcessDefinitionKey();

    /**
     * process definition
     */
    String getProcessDefinitionVersion();

    /**
     * process definition
     */
    String getProcessDefinitionId();

    /**
     * process instance id
     */
    String getProcessInstanceId();
}
