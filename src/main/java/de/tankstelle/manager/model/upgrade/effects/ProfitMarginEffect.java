package de.tankstelle.manager.model.upgrade.effects;

import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.station.GameState;

public class ProfitMarginEffect extends UpgradeEffect {
    public ProfitMarginEffect(String effectId, String description, double magnitude) {
        super(effectId, description, magnitude);
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getEconomyModifiers().addProfitMarginBonus(magnitude);
    }

    @Override
    public void remove(GameState gameState) {
        gameState.getEconomyModifiers().addProfitMarginBonus(-magnitude);
    }

    @Override
    public EffectType getType() {
        return EffectType.PROFIT_MARGIN;
    }
} 