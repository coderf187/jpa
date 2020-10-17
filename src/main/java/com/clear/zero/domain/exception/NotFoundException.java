package com.clear.zero.domain.exception;

public class NotFoundException extends BusinessException {
    private static final String CODE = "not-found";

    public NotFoundException(String message) {
        super(CODE, message);
    }

}
