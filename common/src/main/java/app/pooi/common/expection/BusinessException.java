package app.pooi.common.expection;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code) {
        this.code = code;
    }
}
