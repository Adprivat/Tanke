package de.tankstelle.manager.model.customer;

import de.tankstelle.manager.model.fuel.FuelType;

public class PriceConsciousCustomer extends Customer {
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
        // Kauft durchschnittlich 30 Liter
        return 30.0;
    }
} 