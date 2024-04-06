package com.ch.core.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class Response {

    @Getter
    @Builder
    public static class Body {

        private LocalDateTime timestamp;
        private int state;
        private String result;
        private String message;
        private Object data;
        private Object error;
    }

    public ResponseEntity<Response.Body> success(Object data, String msg, HttpStatus status) {
        Body body = Body.builder()
                .timestamp(LocalDateTime.now())
                .state(status.value())
                .data(data)
                .result("success")
                .message(msg)
                .error(Collections.emptyList())
                .build();

        return ResponseEntity.ok(body);
    }

    public ResponseEntity<Response.Body> success(String msg) {
        return success(Collections.emptyList(), msg, HttpStatus.OK);
    }

    public ResponseEntity<Response.Body> fail(Object data, String msg, HttpStatus status) {
        Body body = Body.builder()
                .timestamp(LocalDateTime.now())
                .state(status.value())
                .data(data)
                .result("fail")
                .message(msg)
                .error(Collections.emptyList())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Response.Body> invalidFields(List<LinkedHashMap<String, String>> errors) {
        Body body = Body.builder()
                .timestamp(LocalDateTime.now())
                .state(HttpStatus.BAD_REQUEST.value())
                .data(Collections.emptyList())
                .result("fail")
                .message("Invalid request fields exist")
                .error(errors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }
}
