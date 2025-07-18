package de.tankstelle.manager.model.upgrade.types;

import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.UpgradeCategory;
import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.station.GameStatistics;
import java.util.List;

public class SupermarketServiceUpgrade extends Upgrade {
    private final double serviceRevenue;

    public SupermarketServiceUpgrade(String id, String name, String description, double cost, List<String> prerequisites, double serviceRevenue) {
        super(id, name, description, cost, UpgradeCategory.CUSTOMER_SERVICE, prerequisites);
        this.serviceRevenue = serviceRevenue;
    }

    @Override
    public List<UpgradeEffect> getEffects() {
        return List.of();
    }

    @Override
    public boolean canInstall(GameState gameState) {
        return !gameState.hasUpgrade(id) && prerequisites.stream().allMatch(gameState::hasUpgrade);
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
        return serviceRevenue * 100; // Beispiel
    }

    public double getServiceRevenue() { return serviceRevenue; }
} 