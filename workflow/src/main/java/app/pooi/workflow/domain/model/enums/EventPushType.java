package app.pooi.workflow.domain.model.enums;

import app.pooi.basic.util.IEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EventPushType implements IEnum<String> {

    HTTP("http"),
    GRPC("grpc"),
    KAFKA("kafka"),
    ;
    @EnumValue
    private final String code;

    @Override
    public String getValue() {
        return code;
    }
}
