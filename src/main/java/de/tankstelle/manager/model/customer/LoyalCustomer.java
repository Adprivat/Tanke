package de.tankstelle.manager.model.customer;

import de.tankstelle.manager.model.fuel.FuelType;

public class LoyalCustomer extends Customer {
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
        // Kauft durchschnittlich 50 Liter
        return 50.0;
    }
} 