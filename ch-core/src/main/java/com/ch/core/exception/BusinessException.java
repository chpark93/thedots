package com.ch.core.exception;

import com.ch.core.model.code.Errors;

public class BusinessException extends RuntimeException {

    private final Errors errorConstants;

    public BusinessException(String message, Errors errorConstants) {
        super(message);
        this.errorConstants = errorConstants;
    }

    public BusinessException(Errors errorConstants) {
        super(errorConstants.getMessage());
        this.errorConstants = errorConstants;
    }

    public Errors getErrorCode() {
        return errorConstants;
    }

}
