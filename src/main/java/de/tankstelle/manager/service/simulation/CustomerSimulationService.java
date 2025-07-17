package de.tankstelle.manager.service.simulation;

import de.tankstelle.manager.model.customer.Customer;
import de.tankstelle.manager.model.fuel.FuelType;
import java.util.ArrayList;
import java.util.List;

public class CustomerSimulationService {
    private final CustomerGenerator generator = new CustomerGenerator();
    private double customerSatisfaction = 1.0; // 1.0 = sehr zufrieden

    public List<Customer> generateCustomers(double priceDelta, int baseFrequency) {
        // PreisDelta: Differenz zum Marktpreis (positiv = teurer)
        int frequency = (int) (baseFrequency * (1.0 - priceDelta));
        frequency = Math.max(1, frequency); // Mindestfrequenz: immer mindestens 1 Kunde
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < frequency; i++) {
            customers.add(generator.generateCustomer());
        }
        return customers;
    }

    public void updateSatisfaction(boolean goodService) {
        if (goodService) {
            customerSatisfaction = Math.min(1.0, customerSatisfaction + 0.05);
        } else {
            customerSatisfaction = Math.max(0.0, customerSatisfaction - 0.1);
        }
    }

    public double getCustomerSatisfaction() {
        return customerSatisfaction;
    }
} 