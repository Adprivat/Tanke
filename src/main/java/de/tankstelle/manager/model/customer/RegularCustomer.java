package de.tankstelle.manager.model.customer;

import de.tankstelle.manager.model.fuel.FuelType;

import java.util.Random;

public class RegularCustomer extends Customer {
    private static final Random purchaseRandom = new Random();

    public RegularCustomer(FuelType fuelPreference) {
        super(CustomerType.REGULAR, 0.5, fuelPreference);
    }

    @Override
    public boolean willBuy(double price, double marketAverage) {
        // Kauft, wenn Preis nicht mehr als 10% über Markt liegt
        return price <= marketAverage * 1.10;
    }

    @Override
    public double calculatePurchaseAmount() {
        // Zufällige Kaufmenge zwischen 5 und 100 Litern
        return 5 + purchaseRandom.nextInt(96); // 5 bis 100 inkl.
    }
} 