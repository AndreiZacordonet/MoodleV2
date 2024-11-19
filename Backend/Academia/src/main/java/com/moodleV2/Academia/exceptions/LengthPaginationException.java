package com.moodleV2.Academia.exceptions;

public class LengthPaginationException extends RuntimeException {
    public LengthPaginationException() {
        super("Length exceeded in pagination parameters.");
    }
}
