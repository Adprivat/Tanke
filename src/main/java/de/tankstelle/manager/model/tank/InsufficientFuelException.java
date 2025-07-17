package de.tankstelle.manager.model.tank;

public class InsufficientFuelException extends Exception {
    public InsufficientFuelException(String message) {
        super(message);
    }
} 