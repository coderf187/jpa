package com.clear.zero.domain.exception;

public class CategoryNotMatchException extends BusinessException {
    private static final String CODE = "category-not-match";

    public CategoryNotMatchException(String message) {
        super(CODE, message);
    }

}
