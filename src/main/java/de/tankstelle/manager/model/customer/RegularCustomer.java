package de.tankstelle.manager.model.customer;

import de.tankstelle.manager.model.fuel.FuelType;

public class RegularCustomer extends Customer {
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
        // Kauft durchschnittlich 40 Liter
        return 40.0;
    }
} 