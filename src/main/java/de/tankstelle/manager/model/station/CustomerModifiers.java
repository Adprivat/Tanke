package de.tankstelle.manager.model.station;

public class CustomerModifiers {
    private double frequencyMultiplier = 1.0;
    private double satisfactionBonus = 0.0;
    private double priceToleranceBonus = 0.0;

    public void addFrequencyMultiplier(double multiplier) {
        this.frequencyMultiplier *= (1.0 + multiplier);
    }

    public void addSatisfactionBonus(double bonus) {
        this.satisfactionBonus += bonus;
    }

    public void addPriceToleranceBonus(double bonus) {
        this.priceToleranceBonus += bonus;
    }

    public double getFrequencyMultiplier() {
        return frequencyMultiplier;
    }

    public double getSatisfactionBonus() {
        return satisfactionBonus;
    }

    public double getPriceToleranceBonus() {
        return priceToleranceBonus;
    }

    public int calculateModifiedCustomerCount(int baseCount) {
        return (int) (baseCount * frequencyMultiplier);
    }
} 