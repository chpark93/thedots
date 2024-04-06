package com.ch.core.common.response;

import com.ch.core.model.code.Errors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int state;
    private String result;
    private String message;
    private Object data;
    private List<FieldError> error;


    private ErrorResponse(final Errors code, final List<FieldError> error) {
        this.timestamp = LocalDateTime.now();
        this.state = code.getStatus();
        this.result = "fail";
        this.message = code.getMessage();
        this.data = Collections.emptyList();
        this.error = error;
    }

    private ErrorResponse(final Errors code) {
        this.timestamp = LocalDateTime.now();
        this.state = code.getStatus();
        this.result = "fail";
        this.message = code.getMessage();
        this.data = Collections.emptyList();
        this.error = Collections.emptyList();
    }


    public static ErrorResponse of(final Errors code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final Errors code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final Errors code, final List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }

    public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : String.valueOf(e.getValue());
        final List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of(e.getName(), value, e.getErrorCode());
        return new ErrorResponse(Errors.BAD_REQUEST, errors);
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .toList();
        }
    }
}
