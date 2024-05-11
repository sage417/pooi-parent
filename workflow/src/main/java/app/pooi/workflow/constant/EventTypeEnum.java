package app.pooi.workflow.constant;

import app.pooi.common.util.IEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventTypeEnum implements IEnum<String> {

    USER_TASK_TURN("USER_TASK_TURN"),
    USER_TASK_COMPLETE("USER_TASK_COMPLETE"),
    ;

    private final String value;

    @Override
    public String getValue() {
        return value;
    }


}
