package de.tankstelle.manager.model.upgrade.effects;

import de.tankstelle.manager.model.upgrade.UpgradeEffect;
import de.tankstelle.manager.model.station.GameState;

public class CustomerFrequencyEffect extends UpgradeEffect {
    public CustomerFrequencyEffect(String effectId, String description, double magnitude) {
        super(effectId, description, magnitude);
    }

    @Override
    public void apply(GameState gameState) {
        gameState.getCustomerModifiers().addFrequencyMultiplier(magnitude);
    }

    @Override
    public void remove(GameState gameState) {
        gameState.getCustomerModifiers().addFrequencyMultiplier(-magnitude);
    }

    @Override
    public EffectType getType() {
        return EffectType.CUSTOMER_FREQUENCY;
    }
} 