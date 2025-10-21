package app.pooi.basic.expection;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String messageCode;

    private Object[] args;

    public BusinessException(String messageCode) {
        this.messageCode = messageCode;
    }

    public BusinessException(String messageCode, Object... args) {
        this.messageCode = messageCode;
        this.args = args;
    }
}
