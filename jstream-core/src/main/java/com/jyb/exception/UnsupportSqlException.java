package com.jyb.exception;

public class UnsupportSqlException extends SqlParseException {
    public UnsupportSqlException(String message) {
        super(message);
    }
}
