package de.tankstelle.manager.util.exception;

public class InsufficientFundsException extends GameException {
    public InsufficientFundsException(String userMessage) {
        super(ErrorCode.INSUFFICIENT_FUNDS, userMessage);
    }
} 