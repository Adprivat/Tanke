package de.tankstelle.manager.service.market;

import de.tankstelle.manager.model.fuel.FuelType;

public class FuelOrder {
    private final FuelType type;
    private final double amount;
    private final double pricePerUnit;
    private final double totalCost;
    private final int deliveryTimeMinutes;

    public FuelOrder(FuelType type, double amount, double pricePerUnit, double totalCost, int deliveryTimeMinutes) {
        this.type = type;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.totalCost = totalCost;
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }

    public FuelType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }
} 