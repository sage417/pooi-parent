package app.pooi.basic.expection;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    private Object[] args;

    public BusinessException(String code) {
        this.code = code;
    }

    public BusinessException(String code, Object... args) {
        this.code = code;
        this.args = args;
    }
}
