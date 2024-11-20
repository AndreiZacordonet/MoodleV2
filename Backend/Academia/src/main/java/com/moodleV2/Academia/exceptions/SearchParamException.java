package com.moodleV2.Academia.exceptions;

public class SearchParamException extends RuntimeException {
    public SearchParamException(Object param) {
        super("Invalid search parameter: " + param);
    }
}
