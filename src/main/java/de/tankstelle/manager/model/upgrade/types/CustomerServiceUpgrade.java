package de.tankstelle.manager.model.upgrade.types;

import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.UpgradeCategory;
import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.upgrade.effects.CustomerFrequencyEffect;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.station.GameStatistics;
import java.util.List;

public class CustomerServiceUpgrade extends Upgrade {
    private final double frequencyBoost;
    private final double satisfactionBoost;
    private final double priceToleranceBoost;

    public CustomerServiceUpgrade(String id, String name, String description, double cost, List<String> prerequisites, double frequencyBoost, double satisfactionBoost, double priceToleranceBoost) {
        super(id, name, description, cost, UpgradeCategory.CUSTOMER_SERVICE, prerequisites);
        this.frequencyBoost = frequencyBoost;
        this.satisfactionBoost = satisfactionBoost;
        this.priceToleranceBoost = priceToleranceBoost;
    }

    @Override
    public List<UpgradeEffect> getEffects() {
        return List.of(
            new CustomerFrequencyEffect(id + "_freq", "Erhöht Kundenfrequenz", frequencyBoost)
            // Weitere Effekte wie Zufriedenheit, Preistoleranz können ergänzt werden
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
        return frequencyBoost * 500;
    }

    public double getFrequencyBoost() { return frequencyBoost; }
    public double getSatisfactionBoost() { return satisfactionBoost; }
    public double getPriceToleranceBoost() { return priceToleranceBoost; }
} 