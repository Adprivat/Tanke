package de.tankstelle.manager.model.upgrade.types;

import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.UpgradeCategory;
import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.upgrade.effects.TankCapacityEffect;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.station.GameStatistics;
import java.util.List;

public class TankCapacityUpgrade extends Upgrade {
    private final FuelType fuelType;
    private final double capacityIncrease;
    private final int level;

    public TankCapacityUpgrade(String id, String name, String description, double cost, List<String> prerequisites, FuelType fuelType, double capacityIncrease, int level) {
        super(id, name, description, cost, UpgradeCategory.TANK_CAPACITY, prerequisites);
        this.fuelType = fuelType;
        this.capacityIncrease = capacityIncrease;
        this.level = level;
    }

    @Override
    public List<UpgradeEffect> getEffects() {
        return List.of(new TankCapacityEffect(id + "_effect", "Erhöht Tankkapazität für " + fuelType, capacityIncrease, fuelType));
    }

    @Override
    public boolean canInstall(GameState gameState) {
        // Beispiel: Prüfe, ob Upgrade noch nicht installiert ist
        return !gameState.hasUpgrade(id);
    }

    @Override
    public void install(GameState gameState) {
        if (!installed && canInstall(gameState)) {
            getEffects().forEach(effect -> effect.apply(gameState));
            installed = true;
            purchaseDate = java.time.LocalDateTime.now();
            gameState.addUpgrade(this);
        }
    }

    @Override
    public double calculateROI(GameStatistics statistics) {
        // Beispiel: ROI-Berechnung kann später angepasst werden
        return capacityIncrease * 0.1;
    }

    public FuelType getFuelType() { return fuelType; }
    public double getCapacityIncrease() { return capacityIncrease; }
    public int getLevel() { return level; }
} 