package com.clear.zero.aop;

import com.clear.zero.domain.exception.BusinessException;
import com.clear.zero.ui.results.ApiResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GlobalDefaultExceptionHandler {
    private static final String UNKNOWN_ERROR_CODE = "unknown-error";
    private static final String SYSTEM_ERROR_INFO = "系统异常，请联系管理员";
    private static final String ILLEGAL_PARAM_CODE = "illegal-param";

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ApiResult illegalParamExceptionHandler(IllegalArgumentException e) throws Exception {
        // log todo
        return ApiResult.error(ILLEGAL_PARAM_CODE, e.getMessage());
    }

    @ExceptionHandler(value = BusinessException.class)
    public ApiResult businessExceptionHandler(BusinessException e) throws Exception {
        // log todo
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResult defaultErrorHandler(Exception e) throws Exception {
        // log todo
        e.printStackTrace();
        return ApiResult.error(UNKNOWN_ERROR_CODE, SYSTEM_ERROR_INFO);
    }
}
