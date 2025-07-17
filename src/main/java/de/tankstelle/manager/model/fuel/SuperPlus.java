package de.tankstelle.manager.model.fuel;

public class SuperPlus extends Fuel {
    public SuperPlus(double basePrice) {
        super("Super Plus", basePrice, FuelType.SUPER_PLUS);
    }

    @Override
    public double calculateSellingPrice(double marketPrice, double margin) {
        return marketPrice + margin;
    }
} 