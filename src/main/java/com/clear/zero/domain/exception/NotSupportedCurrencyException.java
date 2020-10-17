package com.clear.zero.domain.exception;

public class NotSupportedCurrencyException extends BusinessException {
    private static final String CODE = "not-supported-currency";

    public NotSupportedCurrencyException(String message) {
        super(CODE, message);
    }

}
