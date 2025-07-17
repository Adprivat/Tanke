package de.tankstelle.manager.model.station;

import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.tank.FuelTank;

import java.time.LocalDateTime;
import java.util.*;

public class GameState {
    private double cash;
    private Map<FuelType, FuelTank> tanks;
    private Map<FuelType, Double> currentPrices;
    private GameStatistics statistics;
    private LocalDateTime gameTime;
    private final List<GameStateObserver> observers = new ArrayList<>();
    private double customerSatisfaction = 1.0; // 1.0 = sehr zufrieden, 0.0 = sehr unzufrieden

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
} 