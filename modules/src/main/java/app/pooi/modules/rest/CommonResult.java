package app.pooi.modules.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {

    private boolean success;

    private int code;

    private String message;

    private T data;


    public static <R> CommonResult<R> success(R data) {
        return new CommonResult<>(true, 0, "", data);
    }

    public static <R> CommonResult<R> fail(int code, String message, R data) {
        return new CommonResult<>(false, code, message, data);
    }
}
