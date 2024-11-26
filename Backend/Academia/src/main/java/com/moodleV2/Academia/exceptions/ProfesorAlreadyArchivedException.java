package com.moodleV2.Academia.exceptions;

public class ProfesorAlreadyArchivedException extends RuntimeException{
    public ProfesorAlreadyArchivedException(String msg) {
        super(msg);
    }
}
