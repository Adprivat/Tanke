package de.tankstelle.manager.service.upgrade;

import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.station.GameState;
import java.util.List;

public class UpgradeEffectEngine {
    private final GameState gameState;

    public UpgradeEffectEngine(GameState gameState) {
        this.gameState = gameState;
    }

    public void applyEffects(List<UpgradeEffect> effects) {
        effects.forEach(effect -> effect.apply(gameState));
        recalculateGameState();
    }

    public void removeEffects(List<UpgradeEffect> effects) {
        effects.forEach(effect -> effect.remove(gameState));
        recalculateGameState();
    }

    private void recalculateGameState() {
        // Hier können alle abgeleiteten Werte neu berechnet werden
        // z.B. gameState.recalculateModifiers();
        // gameState.notifyObservers();
    }
} 