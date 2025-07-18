package de.tankstelle.manager.model.station;

public class EconomyModifiers {
    private double profitMarginBonus = 0.0;
    private double operatingCostReduction = 0.0;
    private double fuelCostReduction = 0.0;

    public void addProfitMarginBonus(double bonus) {
        this.profitMarginBonus += bonus;
    }

    public void addOperatingCostReduction(double reduction) {
        this.operatingCostReduction += reduction;
    }

    public void addFuelCostReduction(double reduction) {
        this.fuelCostReduction += reduction;
    }

    public double getProfitMarginBonus() {
        return profitMarginBonus;
    }

    public double getOperatingCostReduction() {
        return operatingCostReduction;
    }

    public double getFuelCostReduction() {
        return fuelCostReduction;
    }

    public double calculateModifiedProfit(double baseProfit) {
        return baseProfit * (1.0 + profitMarginBonus);
    }
} 