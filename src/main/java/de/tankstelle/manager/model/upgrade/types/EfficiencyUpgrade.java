package de.tankstelle.manager.model.upgrade.types;

import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.UpgradeCategory;
import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.upgrade.effects.ProfitMarginEffect;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.station.GameStatistics;
import java.util.List;

public class EfficiencyUpgrade extends Upgrade {
    private final double costReduction;
    private final double marginImprovement;

    public EfficiencyUpgrade(String id, String name, String description, double cost, List<String> prerequisites, double costReduction, double marginImprovement) {
        super(id, name, description, cost, UpgradeCategory.EFFICIENCY, prerequisites);
        this.costReduction = costReduction;
        this.marginImprovement = marginImprovement;
    }

    @Override
    public List<UpgradeEffect> getEffects() {
        return List.of(
            new ProfitMarginEffect(id + "_margin", "Erhöht Gewinnmarge", marginImprovement)
            // Weitere Effekte wie Betriebskostenreduktion können ergänzt werden
        );
    }

    @Override
    public boolean canInstall(GameState gameState) {
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
        return marginImprovement * 1000;
    }

    public double getCostReduction() { return costReduction; }
    public double getMarginImprovement() { return marginImprovement; }
} 