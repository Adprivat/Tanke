package de.tankstelle.manager.model.fuel;

public class Diesel extends Fuel {
    public Diesel(double basePrice) {
        super("Diesel", basePrice, FuelType.DIESEL);
    }

    @Override
    public double calculateSellingPrice(double marketPrice, double margin) {
        return marketPrice + margin;
    }
} 