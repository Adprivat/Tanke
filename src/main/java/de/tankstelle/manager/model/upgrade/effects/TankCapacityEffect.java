package de.tankstelle.manager.model.upgrade.effects;

import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.tank.FuelTank;

public class TankCapacityEffect extends UpgradeEffect {
    private final FuelType fuelType;

    public TankCapacityEffect(String effectId, String description, double magnitude, FuelType fuelType) {
        super(effectId, description, magnitude);
        this.fuelType = fuelType;
    }

    @Override
    public void apply(GameState gameState) {
        FuelTank tank = gameState.getTanks().get(fuelType);
        if (tank != null) {
            tank.increaseCapacity(magnitude);
        }
    }

    @Override
    public void remove(GameState gameState) {
        FuelTank tank = gameState.getTanks().get(fuelType);
        if (tank != null) {
            tank.increaseCapacity(-magnitude);
        }
    }

    @Override
    public EffectType getType() {
        return EffectType.TANK_CAPACITY;
    }

    public FuelType getFuelType() { return fuelType; }
} 