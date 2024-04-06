package com.ch.core.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class HelperUtil {

    private HelperUtil() {}

    public static List<LinkedHashMap<String, String>> refineErrors(Errors errors) {
        LinkedList<LinkedHashMap<String, String>> errorList = new LinkedList<>();
        errors.getFieldErrors().forEach(e-> {
            LinkedHashMap<String, String> error = new LinkedHashMap<>();
            error.put("field", e.getField());
            error.put("message", e.getDefaultMessage());
            errorList.push(error);
        });

        return errorList;
    }
}
