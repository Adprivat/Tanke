package de.tankstelle.manager.model.tank;

public interface TankObserver {
    void onLevelChanged(FuelTank tank, double newLevel);
    void onLowLevel(FuelTank tank);
    void onEmpty(FuelTank tank);
} 