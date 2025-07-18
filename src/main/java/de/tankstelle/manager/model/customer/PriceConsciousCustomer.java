package de.tankstelle.manager.model.customer;

import de.tankstelle.manager.model.fuel.FuelType;

import java.util.Random;

public class PriceConsciousCustomer extends Customer {
    private static final Random purchaseRandom = new Random();

    public PriceConsciousCustomer(FuelType fuelPreference) {
        super(CustomerType.PRICE_CONSCIOUS, 0.9, fuelPreference);
    }

    @Override
    public boolean willBuy(double price, double marketAverage) {
        // Kauft nur, wenn Preis maximal 2% über Markt liegt
        return price <= marketAverage * 1.02;
    }

    @Override
    public double calculatePurchaseAmount() {
        // Zufällige Kaufmenge zwischen 5 und 100 Litern
        return 5 + purchaseRandom.nextInt(96); // 5 bis 100 inkl.
    }
} 