package de.tankstelle.manager.model.upgrade.types;

import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.UpgradeCategory;
import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.station.GameStatistics;
import java.util.List;

public class PriceAutomationUpgrade extends Upgrade {
    private final FuelType fuelType;

    public PriceAutomationUpgrade(String id, String name, String description, double cost, List<String> prerequisites, FuelType fuelType) {
        super(id, name, description, cost, UpgradeCategory.TECHNOLOGY, prerequisites);
        this.fuelType = fuelType;
    }

    @Override
    public List<UpgradeEffect> getEffects() {
        // Keine direkten Effekte, sondern Freischaltung der Preisautomatisierung
        return List.of();
    }

    @Override
    public boolean canInstall(GameState gameState) {
        // Nur einmal pro Kraftstoffart installierbar
        return !gameState.hasUpgrade(id);
    }

    @Override
    public void install(GameState gameState) {
        if (!installed && canInstall(gameState)) {
            installed = true;
            purchaseDate = java.time.LocalDateTime.now();
            gameState.addUpgrade(this);
        }
    }

    @Override
    public double calculateROI(GameStatistics statistics) {
        return 0; // Kann individuell angepasst werden
    }

    public FuelType getFuelType() { return fuelType; }
} 