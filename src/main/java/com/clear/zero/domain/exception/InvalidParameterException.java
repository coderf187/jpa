package com.clear.zero.domain.exception;

public class InvalidParameterException extends BusinessException {
    private static final String CODE = "invalid-parameter";

    public InvalidParameterException(String message) {
        super(CODE, message);
    }

}
