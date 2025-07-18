package de.tankstelle.manager.model.customer;

import de.tankstelle.manager.model.fuel.FuelType;
import java.util.Random;

public abstract class Customer {
    protected CustomerType type;
    protected double priceSensitivity; // 0 = egal, 1 = extrem preissensibel
    protected FuelType fuelPreference;
    // Gemeinsamer Zufallsgenerator für alle Kunden
    protected static final Random purchaseRandom = new Random();

    public Customer(CustomerType type, double priceSensitivity, FuelType fuelPreference) {
        this.type = type;
        this.priceSensitivity = priceSensitivity;
        this.fuelPreference = fuelPreference;
    }

    public CustomerType getType() {
        return type;
    }

    public double getPriceSensitivity() {
        return priceSensitivity;
    }

    public FuelType getFuelPreference() {
        return fuelPreference;
    }

    public abstract boolean willBuy(double price, double marketAverage);
    public abstract double calculatePurchaseAmount();
} 