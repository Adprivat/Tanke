package de.tankstelle.manager.model.upgrade;

public enum UpgradeCategory {
    TANK_CAPACITY("Tankkapazität"),
    EFFICIENCY("Effizienz"),
    CUSTOMER_SERVICE("Kundenservice"),
    TECHNOLOGY("Technologie");

    private final String displayName;
    UpgradeCategory(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
} 