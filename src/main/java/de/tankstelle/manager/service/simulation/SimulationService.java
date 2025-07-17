package de.tankstelle.manager.service.simulation;

import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.service.market.MarketService;
import de.tankstelle.manager.model.station.GameStatistics;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.tank.FuelTank;
import de.tankstelle.manager.model.customer.Customer;
import de.tankstelle.manager.model.tank.InsufficientFuelException;
import java.util.List;
import java.util.function.Consumer;
import java.util.EnumMap;
import java.util.Map;

public class SimulationService implements Runnable {
    private final GameState gameState;
    private final CustomerSimulationService customerSimulationService;
    private final MarketService marketService;
    private boolean running = false;
    private Thread thread;
    private Consumer<String> logConsumer;
    private int tickCounter = 0;
    private Map<FuelType, Double> lastMarketPrices = new EnumMap<>(FuelType.class);
    private Consumer<Boolean> onMarketPriceChangeCallback;
    private boolean firstMarketUpdateDone = false;

    public SimulationService(GameState gameState, CustomerSimulationService customerSimulationService, MarketService marketService) {
        this.gameState = gameState;
        this.customerSimulationService = customerSimulationService;
        this.marketService = marketService;
    }

    public void startSimulation() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    public void pauseSimulation() {
        running = false;
    }

    public void setLogConsumer(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }

    public void setOnMarketPriceChangeCallback(Consumer<Boolean> callback) {
        this.onMarketPriceChangeCallback = callback;
    }

    @Override
    public void run() {
        while (running) {
            updateGameState();
            try {
                Thread.sleep(1000); // 1 Sekunde pro Tick
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void updateGameState() {
        tickCounter++;
        boolean marketPriceChanged = false;
        if (tickCounter % 120 == 1) { // alle 120 Ticks (2 Minuten)
            if (!firstMarketUpdateDone) {
                // Nur initialisieren, aber keine Callback/Meldung
                for (FuelType type : FuelType.values()) {
                    lastMarketPrices.put(type, marketService.getCurrentMarketPrice(type));
                }
                marketService.updateMarketPrices();
                for (FuelType type : FuelType.values()) {
                    lastMarketPrices.put(type, marketService.getCurrentMarketPrice(type));
                }
                firstMarketUpdateDone = true;
                return;
            }
            // Vorherige Preise merken
            for (FuelType type : FuelType.values()) {
                lastMarketPrices.put(type, marketService.getCurrentMarketPrice(type));
            }
            marketService.updateMarketPrices();
            // Prüfen, ob sich ein Preis geändert hat
            for (FuelType type : FuelType.values()) {
                double oldPrice = lastMarketPrices.get(type);
                double newPrice = marketService.getCurrentMarketPrice(type);
                if (Math.abs(newPrice - oldPrice) > 0.0001) {
                    marketPriceChanged = true;
                    if (logConsumer != null) {
                        logConsumer.accept("WELTMARKTPREIS-ÄNDERUNG: Neuer Preis für " + typeToString(type) + ": " + String.format("%.2f", newPrice) + " €/L");
                    }
                    if (onMarketPriceChangeCallback != null) {
                        onMarketPriceChangeCallback.accept(true);
                    }
                }
            }
        }
        // Für jede Kraftstoffart Kunden generieren und Verkäufe simulieren
        for (FuelType type : FuelType.values()) {
            double marketPrice = marketService.getCurrentMarketPrice(type);
            double stationPrice = gameState.getCurrentPrices().getOrDefault(type, marketPrice + 0.10);
            double priceDelta = (stationPrice - marketPrice) / marketPrice;
            int baseFrequency = 3; // Beispielwert
            List<Customer> customers = customerSimulationService.generateCustomers(priceDelta, baseFrequency);
            FuelTank tank = gameState.getTanks().get(type);
            GameStatistics stats = gameState.getStatistics();
            for (Customer customer : customers) {
                if (customer.getFuelPreference() == type) {
                    double amount = customer.calculatePurchaseAmount();
                    if (priceDelta <= 0.10) {
                        // Sehr zufrieden, kauft
                        try {
                            tank.dispenseFuel(amount);
                            double profit = (stationPrice - marketPrice) * amount;
                            double revenue = stationPrice * amount;
                            stats.recordSale(type, amount, profit);
                            gameState.setCash(gameState.getCash() + revenue);
                            gameState.addSatisfactionDelta(+0.01);
                            if (logConsumer != null) {
                                String log = String.format("Kunde kaufte %.0f L %s für %.2f € und war sehr zufrieden.", amount, typeToString(type), revenue);
                                logConsumer.accept(log);
                            }
                        } catch (Exception e) {
                            gameState.addSatisfactionDelta(-0.07);
                            if (logConsumer != null) {
                                String log = String.format("Kunde wollte %.0f L %s, aber der Tank war leer und er ging frustriert.", amount, typeToString(type));
                                logConsumer.accept(log);
                            }
                        }
                    } else if (priceDelta <= 0.20) {
                        // Zufrieden, kauft
                        try {
                            tank.dispenseFuel(amount);
                            double profit = (stationPrice - marketPrice) * amount;
                            double revenue = stationPrice * amount;
                            stats.recordSale(type, amount, profit);
                            gameState.setCash(gameState.getCash() + revenue);
                            gameState.addSatisfactionDelta(+0.005);
                            if (logConsumer != null) {
                                String log = String.format("Kunde kaufte %.0f L %s für %.2f € und war zufrieden.", amount, typeToString(type), revenue);
                                logConsumer.accept(log);
                            }
                        } catch (Exception e) {
                            gameState.addSatisfactionDelta(-0.07);
                            if (logConsumer != null) {
                                String log = String.format("Kunde wollte %.0f L %s, aber der Tank war leer und er ging frustriert.", amount, typeToString(type));
                                logConsumer.accept(log);
                            }
                        }
                    } else if (priceDelta <= 0.30) {
                        // Kauft, aber unzufrieden
                        try {
                            tank.dispenseFuel(amount);
                            double profit = (stationPrice - marketPrice) * amount;
                            double revenue = stationPrice * amount;
                            stats.recordSale(type, amount, profit);
                            gameState.setCash(gameState.getCash() + revenue);
                            gameState.addSatisfactionDelta(-0.02);
                            if (logConsumer != null) {
                                String log = String.format("Kunde kaufte %.0f L %s für %.2f € war aber unzufrieden wegen des hohen Preises.", amount, typeToString(type), revenue);
                                logConsumer.accept(log);
                            }
                        } catch (Exception e) {
                            gameState.addSatisfactionDelta(-0.07);
                            if (logConsumer != null) {
                                String log = String.format("Kunde wollte %.0f L %s, aber der Tank war leer und er ging frustriert.", amount, typeToString(type));
                                logConsumer.accept(log);
                            }
                        }
                    } else {
                        // Preis zu hoch, kauft nicht
                        gameState.addSatisfactionDelta(-0.07);
                        if (logConsumer != null) {
                            String log = String.format("Kunde wollte %.0f L %s, aber der Preis war viel zu hoch und er ging sehr unzufrieden.", amount, typeToString(type));
                            logConsumer.accept(log);
                        }
                    }
                }
            }
        }
        // Nach jedem Tick Observer benachrichtigen
        gameState.notifyObservers();
    }

    private String typeToString(FuelType type) {
        switch (type) {
            case SUPER_95: return "Super 95";
            case SUPER_95_E10: return "Super 95 E10";
            case SUPER_PLUS: return "Super Plus";
            case DIESEL: return "Diesel";
            default: return type.name();
        }
    }
} 