package de.tankstelle.manager.model.station;

import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.tank.FuelTank;
import de.tankstelle.manager.model.upgrade.Upgrade;

import java.time.LocalDateTime;
import java.util.*;
import java.util.EnumMap;

public class GameState {
    private double cash;
    private Map<FuelType, FuelTank> tanks;
    private Map<FuelType, Double> currentPrices;
    private GameStatistics statistics;
    private LocalDateTime gameTime;
    private final List<GameStateObserver> observers = new ArrayList<>();
    private double customerSatisfaction = 1.0; // 1.0 = sehr zufrieden, 0.0 = sehr unzufrieden
    private final Map<String, Upgrade> installedUpgrades = new HashMap<>();
    private final EconomyModifiers economyModifiers = new EconomyModifiers();
    private final CustomerModifiers customerModifiers = new CustomerModifiers();
    // Automatisierungs-Status und Schwellenwerte pro Kraftstoffart
    private final Map<FuelType, Boolean> automationEnabled = new EnumMap<>(FuelType.class);
    private final Map<FuelType, Double> automationThreshold = new EnumMap<>(FuelType.class);
    // Preisautomatisierung pro Kraftstoffart
    private final Map<FuelType, Boolean> priceAutomationEnabled = new EnumMap<>(FuelType.class);
    private final Map<FuelType, Double> priceAutomationMargin = new EnumMap<>(FuelType.class);

    public GameState(double initialCash, Map<FuelType, FuelTank> tanks, Map<FuelType, Double> prices, GameStatistics statistics) {
        this.cash = initialCash;
        this.tanks = tanks;
        this.currentPrices = prices;
        this.statistics = statistics;
        this.gameTime = LocalDateTime.now();
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
        notifyObservers();
    }

    public Map<FuelType, FuelTank> getTanks() {
        return tanks;
    }

    public void setTanks(Map<FuelType, FuelTank> tanks) {
        this.tanks = tanks;
        notifyObservers();
    }

    public Map<FuelType, Double> getCurrentPrices() {
        return currentPrices;
    }

    public void setCurrentPrices(Map<FuelType, Double> currentPrices) {
        this.currentPrices = currentPrices;
        notifyObservers();
    }

    public GameStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(GameStatistics statistics) {
        this.statistics = statistics;
        notifyObservers();
    }

    public LocalDateTime getGameTime() {
        return gameTime;
    }

    public void setGameTime(LocalDateTime gameTime) {
        this.gameTime = gameTime;
        notifyObservers();
    }

    public double getCustomerSatisfaction() { return customerSatisfaction; }
    public void setCustomerSatisfaction(double value) {
        customerSatisfaction = Math.max(0.0, Math.min(1.0, value));
        notifyObservers();
    }
    public void addSatisfactionDelta(double delta) {
        setCustomerSatisfaction(customerSatisfaction + delta);
    }

    public void addObserver(GameStateObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (GameStateObserver observer : observers) {
            observer.onGameStateChanged(this);
        }
    }

    public void addUpgrade(Upgrade upgrade) {
        installedUpgrades.put(upgrade.getId(), upgrade);
        // Optional: Observer-Benachrichtigung
    }

    public boolean hasUpgrade(String upgradeId) {
        return installedUpgrades.containsKey(upgradeId);
    }

    public List<Upgrade> getInstalledUpgrades() {
        return new ArrayList<>(installedUpgrades.values());
    }

    public EconomyModifiers getEconomyModifiers() {
        return economyModifiers;
    }

    public CustomerModifiers getCustomerModifiers() {
        return customerModifiers;
    }

    public boolean isAutomationEnabled(FuelType type) {
        return automationEnabled.getOrDefault(type, false);
    }
    public void setAutomationEnabled(FuelType type, boolean enabled) {
        automationEnabled.put(type, enabled);
        notifyObservers();
    }
    public double getAutomationThreshold(FuelType type) {
        return automationThreshold.getOrDefault(type, 0.2); // Default 20%
    }
    public void setAutomationThreshold(FuelType type, double threshold) {
        automationThreshold.put(type, threshold);
        notifyObservers();
    }

    public boolean isPriceAutomationEnabled(FuelType type) {
        return priceAutomationEnabled.getOrDefault(type, false);
    }
    public void setPriceAutomationEnabled(FuelType type, boolean enabled) {
        priceAutomationEnabled.put(type, enabled);
        notifyObservers();
    }
    public double getPriceAutomationMargin(FuelType type) {
        return priceAutomationMargin.getOrDefault(type, 10.0); // Default 10%
    }
    public void setPriceAutomationMargin(FuelType type, double margin) {
        priceAutomationMargin.put(type, margin);
        notifyObservers();
    }
} 