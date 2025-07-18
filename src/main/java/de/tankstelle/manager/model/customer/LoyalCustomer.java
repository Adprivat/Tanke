package de.tankstelle.manager.model.customer;

import de.tankstelle.manager.model.fuel.FuelType;

import java.util.Random;

public class LoyalCustomer extends Customer {
    private static final Random purchaseRandom = new Random();

    public LoyalCustomer(FuelType fuelPreference) {
        super(CustomerType.LOYAL, 0.2, fuelPreference);
    }

    @Override
    public boolean willBuy(double price, double marketAverage) {
        // Kauft auch bei 20% über Marktpreis
        return price <= marketAverage * 1.20;
    }

    @Override
    public double calculatePurchaseAmount() {
        // Zufällige Kaufmenge zwischen 5 und 100 Litern
        return 5 + purchaseRandom.nextInt(96); // 5 bis 100 inkl.
    }
} 