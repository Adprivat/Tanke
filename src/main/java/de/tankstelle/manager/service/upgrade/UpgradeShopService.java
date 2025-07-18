package de.tankstelle.manager.service.upgrade;

import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.UpgradeCategory;
import de.tankstelle.manager.model.station.GameState;
import java.util.List;
import java.util.stream.Collectors;

public class UpgradeShopService {
    protected final List<Upgrade> allUpgrades;
    protected final GameState gameState;

    public UpgradeShopService(List<Upgrade> allUpgrades, GameState gameState) {
        this.allUpgrades = allUpgrades;
        this.gameState = gameState;
    }

    public List<Upgrade> getAvailableUpgrades(UpgradeCategory category) {
        return allUpgrades.stream()
            .filter(upg -> upg.getCategory() == category)
            .filter(upg -> upg.canInstall(gameState))
            .filter(upg -> !gameState.hasUpgrade(upg.getId()))
            .collect(Collectors.toList());
    }

    public PurchaseResult purchaseUpgrade(String upgradeId) {
        Upgrade upgrade = allUpgrades.stream()
            .filter(upg -> upg.getId().equals(upgradeId))
            .findFirst().orElse(null);
        if (upgrade == null) return PurchaseResult.NOT_FOUND;
        if (!canAfford(upgrade)) return PurchaseResult.INSUFFICIENT_FUNDS;
        if (!upgrade.canInstall(gameState)) return PurchaseResult.PREREQUISITES_NOT_MET;
        gameState.setCash(gameState.getCash() - upgrade.getCost());
        upgrade.install(gameState);
        return PurchaseResult.SUCCESS;
    }

    protected boolean canAfford(Upgrade upgrade) {
        return gameState.getCash() >= upgrade.getCost();
    }

    public enum PurchaseResult {
        SUCCESS,
        INSUFFICIENT_FUNDS,
        PREREQUISITES_NOT_MET,
        NOT_FOUND
    }
} 