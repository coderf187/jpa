package com.clear.zero.ui.results;

import lombok.Data;

@Data
public class ApiResult<T> {
    private String code;
    private String info;
    private T data;

    public ApiResult() {
    }

    public ApiResult(String code, String info, T data) {
        this.code = code;
        this.info = info;
        this.data = data;
    }

    public static <T> ApiResult ok(T data) {
        return new ApiResult("", "success", data);
    }

    public static <T> ApiResult error(String code, String message) {
        return new ApiResult(code, message, null);
    }
}
