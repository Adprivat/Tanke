package de.tankstelle.manager.model.fuel;

public abstract class Fuel {
    protected String name;
    protected double basePrice;
    protected FuelType type;

    public Fuel(String name, double basePrice, FuelType type) {
        this.name = name;
        this.basePrice = basePrice;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public FuelType getType() {
        return type;
    }

    public abstract double calculateSellingPrice(double marketPrice, double margin);
} 