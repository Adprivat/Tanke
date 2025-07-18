package de.tankstelle.manager.model.upgrade;

import java.time.LocalDateTime;
import java.util.List;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.station.GameStatistics;

public abstract class Upgrade {
    protected final String id;
    protected final String name;
    protected final String description;
    protected final double cost;
    protected final UpgradeCategory category;
    protected final List<String> prerequisites;
    protected boolean installed;
    protected LocalDateTime purchaseDate;

    public Upgrade(String id, String name, String description, double cost, UpgradeCategory category, List<String> prerequisites) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.category = category;
        this.prerequisites = prerequisites;
        this.installed = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getCost() { return cost; }
    public UpgradeCategory getCategory() { return category; }
    public List<String> getPrerequisites() { return prerequisites; }
    public boolean isInstalled() { return installed; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }

    public abstract List<UpgradeEffect> getEffects();
    public abstract boolean canInstall(GameState gameState);
    public abstract void install(GameState gameState);
    public abstract double calculateROI(GameStatistics statistics);
} 