package de.tankstelle.manager.model.fuel;

public class Super95 extends Fuel {
    public Super95(double basePrice) {
        super("Super 95", basePrice, FuelType.SUPER_95);
    }

    @Override
    public double calculateSellingPrice(double marketPrice, double margin) {
        return marketPrice + margin;
    }
} 