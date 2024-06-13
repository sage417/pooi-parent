package app.pooi.workflow.configuration.flowable.engine;

import app.pooi.common.util.IEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum EventTypeEnum implements IEnum<String> {
    INSTANCE_STARTED("INSTANCE_STARTED"),
    INSTANCE_COMPLETED("INSTANCE_COMPLETED"),
    ACTIVITY_STARTED("ACTIVITY_STARTED"),
    ACTIVITY_COMPLETED("ACTIVITY_COMPLETED"),
    USER_TASK_CREATED("USER_TASK_CREATED"),
    USER_TASK_ASSIGNEE("USER_TASK_ASSIGNEE"),
    USER_TASK_COMPLETE("USER_TASK_COMPLETE"),


    OPERATION_USER_TASK_TRANSFER("OPERATION_USER_TASK_TRANSFER");

    private final String value;

    @Override
    public String getValue() {
        return value;
    }


}
