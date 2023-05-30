package com.heartape.exception;

/**
 * jwt解析异常
 */
public class JwtParseException extends RuntimeException {

    public JwtParseException() {
    }

    public JwtParseException(String message) {
        super(message);
    }
}
