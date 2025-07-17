package de.tankstelle.manager.util.exception;

public abstract class GameException extends Exception {
    protected final ErrorCode errorCode;
    protected final String userMessage;

    public GameException(ErrorCode errorCode, String userMessage) {
        super(userMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public enum ErrorCode {
        INSUFFICIENT_FUEL,
        INSUFFICIENT_FUNDS,
        INVALID_PRICE,
        MARKET_UNAVAILABLE
    }
} 