package de.tankstelle.manager.util.exception;

public class MarketUnavailableException extends GameException {
    public MarketUnavailableException(String userMessage) {
        super(ErrorCode.MARKET_UNAVAILABLE, userMessage);
    }
} 