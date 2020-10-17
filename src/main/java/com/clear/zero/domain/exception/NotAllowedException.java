package com.clear.zero.domain.exception;

public class NotAllowedException extends BusinessException {
    private static final String CODE = "not-allowed";

    public NotAllowedException(String message) {
        super(CODE, message);
    }

}
