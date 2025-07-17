package de.tankstelle.manager.model.fuel;

public class Super95E10 extends Fuel {
    public Super95E10(double basePrice) {
        super("Super 95 E10", basePrice, FuelType.SUPER_95_E10);
    }

    @Override
    public double calculateSellingPrice(double marketPrice, double margin) {
        return marketPrice + margin;
    }
} 