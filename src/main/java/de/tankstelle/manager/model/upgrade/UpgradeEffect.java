package de.tankstelle.manager.model.upgrade;

import de.tankstelle.manager.model.station.GameState;

public abstract class UpgradeEffect {
    protected final String effectId;
    protected final String description;
    protected final double magnitude;

    public UpgradeEffect(String effectId, String description, double magnitude) {
        this.effectId = effectId;
        this.description = description;
        this.magnitude = magnitude;
    }

    public String getEffectId() { return effectId; }
    public String getDescription() { return description; }
    public double getMagnitude() { return magnitude; }

    public abstract void apply(GameState gameState);
    public abstract void remove(GameState gameState);
    public abstract EffectType getType();

    public enum EffectType {
        TANK_CAPACITY,
        PROFIT_MARGIN,
        OPERATING_COST,
        CUSTOMER_FREQUENCY,
        CUSTOMER_SATISFACTION,
        PRICE_TOLERANCE,
        AUTOMATION,
        ANALYTICS
    }
} 