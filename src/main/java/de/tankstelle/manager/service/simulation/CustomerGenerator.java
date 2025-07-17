package de.tankstelle.manager.service.simulation;

import de.tankstelle.manager.model.customer.*;
import de.tankstelle.manager.model.fuel.FuelType;

import java.util.Random;

public class CustomerGenerator {
    private final Random random = new Random();

    public Customer generateCustomer() {
        int type = random.nextInt(3);
        FuelType fuelPreference = FuelType.values()[random.nextInt(FuelType.values().length)];
        switch (type) {
            case 0:
                return new RegularCustomer(fuelPreference);
            case 1:
                return new PriceConsciousCustomer(fuelPreference);
            case 2:
            default:
                return new LoyalCustomer(fuelPreference);
        }
    }
} 