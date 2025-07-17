package de.tankstelle.manager.util.exception;

public class InvalidPriceException extends GameException {
    public InvalidPriceException(String userMessage) {
        super(ErrorCode.INVALID_PRICE, userMessage);
    }
} 