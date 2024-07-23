package com.oo.tools.spring.boot;

public class CheckRequestException extends RuntimeException{
    public CheckRequestException() {
        super();
    }

    public CheckRequestException(String message) {
        super(message);
    }

    public CheckRequestException(Throwable cause) {
        super(cause);
    }

    public CheckRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}