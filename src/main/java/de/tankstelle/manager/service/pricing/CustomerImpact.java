package de.tankstelle.manager.service.pricing;

public class CustomerImpact {
    private final double expectedCustomerChange;
    private final String message;

    public CustomerImpact(double expectedCustomerChange, String message) {
        this.expectedCustomerChange = expectedCustomerChange;
        this.message = message;
    }

    public double getExpectedCustomerChange() {
        return expectedCustomerChange;
    }

    public String getMessage() {
        return message;
    }
} 