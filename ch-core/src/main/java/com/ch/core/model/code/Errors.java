package com.ch.core.model.code;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Errors {

    BAD_REQUEST(400, "C001", "Bad Request"),
    UNAUTHORIZED(401, "C002", "Unauthorized"),
    FORBIDDEN(403, "C003", "Forbidden"),
    NOT_FOUND(404, "C004", "Not Found"),
    METHOD_NOT_ALLOWED(405, "C005", "Method Not Allowed"),
    REQUEST_TIMEOUT(408, "C006", "Request Timeout"),
    TEMPORARILY_UNAVAILABLE(480, "C007", "Temporarily Unavailable"),
    INTERNAL_SERVER_ERROR(500, "C008", "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "C009", "Service Unavailable"),

    // Reservation
    DUPLICATED_USER_AND_COURSE(400, "R001", "This Course Is Already Booked"),
    NOT_FOUND_USER(400, "R002", "Not Found User"),
    NOT_FOUND_RESERVATION_INFO(400, "R003", "Not Found Reservation Information"),
    NOT_POSSIBLE_CANCEL(400, "R004", "Cancel Is Not Possible"),
    NOT_POSSIBLE_RESERVATION(400, "R005", "Reservation Is Not Possible"),
    NOT_FOUND_STORE_COURSE(400, "R006", "Not Found Store Course"),

    ;

    private final String message;
    private final String code;
    private final int status;

    Errors(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(Errors::getCode, Errors::getMessage))
    );

    public static Errors of(final String code) {
        return Errors.valueOf(CODE_MAP.get(code));
    }


    public int getStatus() {
        return status;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

}
