package app.pooi.basic.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {

    private boolean success;

    private String code;

    private String message;

    private String path;

    private T data;


    public static <R> CommonResult<R> success(R data) {
        return new CommonResult<>(true, "0", "", null, data);
    }

    public static <R> CommonResult<R> fail(String code, String message, String path, R data) {
        return new CommonResult<>(false, code, message, path, data);
    }
}
