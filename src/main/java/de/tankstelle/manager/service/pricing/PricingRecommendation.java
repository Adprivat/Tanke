package de.tankstelle.manager.service.pricing;

public class PricingRecommendation {
    private final double recommendedPrice;
    private final String reason;

    public PricingRecommendation(double recommendedPrice, String reason) {
        this.recommendedPrice = recommendedPrice;
        this.reason = reason;
    }

    public double getRecommendedPrice() {
        return recommendedPrice;
    }

    public String getReason() {
        return reason;
    }
} 