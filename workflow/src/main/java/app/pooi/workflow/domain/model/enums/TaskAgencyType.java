package app.pooi.workflow.domain.model.enums;

import app.pooi.basic.util.IEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TaskAgencyType implements IEnum<Integer> {

    NONE(0),
    DELEGATE(1),
    SHARE(2),
    ;

    private final int value;


    @Override
    public Integer getValue() {
        return value;
    }
}
