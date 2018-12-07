package com.jyb.exception;

public class LostConfException extends SqlParseException {
    public LostConfException(String message) {
        super(message);
    }

    public LostConfException(String message, Throwable cause) {
        super(message, cause);
    }
}
