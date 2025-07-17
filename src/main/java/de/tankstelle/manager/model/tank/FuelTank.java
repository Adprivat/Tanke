package de.tankstelle.manager.model.tank;

import de.tankstelle.manager.model.fuel.Fuel;
import java.util.ArrayList;
import java.util.List;

public class FuelTank {
    private final Fuel fuelType;
    private double currentLevel;
    private final double capacity;
    private final List<TankObserver> observers = new ArrayList<>();

    public FuelTank(Fuel fuelType, double capacity, double initialLevel) {
        this.fuelType = fuelType;
        this.capacity = capacity;
        this.currentLevel = Math.min(initialLevel, capacity);
    }

    public synchronized void dispenseFuel(double amount) throws InsufficientFuelException {
        if (amount > currentLevel) {
            notifyEmpty();
            throw new InsufficientFuelException("Nicht genug Kraftstoff im Tank.");
        }
        currentLevel -= amount;
        notifyLevelChanged();
        if (currentLevel <= 0) {
            currentLevel = 0;
            notifyEmpty();
        } else if (getFillPercentage() < 0.2) {
            notifyLowLevel();
        }
    }

    public synchronized void refill(double amount) {
        currentLevel = Math.min(currentLevel + amount, capacity);
        notifyLevelChanged();
    }

    public double getFillPercentage() {
        return currentLevel / capacity;
    }

    public double getCurrentLevel() {
        return currentLevel;
    }

    public double getCapacity() {
        return capacity;
    }

    public Fuel getFuelType() {
        return fuelType;
    }

    public void addObserver(TankObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TankObserver observer) {
        observers.remove(observer);
    }

    private void notifyLevelChanged() {
        for (TankObserver observer : observers) {
            observer.onLevelChanged(this, currentLevel);
        }
    }

    private void notifyLowLevel() {
        for (TankObserver observer : observers) {
            observer.onLowLevel(this);
        }
    }

    private void notifyEmpty() {
        for (TankObserver observer : observers) {
            observer.onEmpty(this);
        }
    }
} 