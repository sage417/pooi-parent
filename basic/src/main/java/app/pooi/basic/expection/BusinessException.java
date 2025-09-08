package app.pooi.basic.expection;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code) {
        this.code = code;
    }
}
