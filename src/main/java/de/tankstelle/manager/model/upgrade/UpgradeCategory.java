package de.tankstelle.manager.model.upgrade;

public enum UpgradeCategory {
    TANK_CAPACITY("Tankgrößen"),
    EFFICIENCY("Werkstatt"),
    CUSTOMER_SERVICE("Einkauf"),
    TECHNOLOGY("Technologie"),
    CAR_WASH("Waschstraße");

    private final String displayName;
    UpgradeCategory(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
} 