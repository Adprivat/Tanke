package de.tankstelle.manager.service.simulation;

import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.service.market.MarketService;
import de.tankstelle.manager.model.station.GameStatistics;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.tank.FuelTank;
import de.tankstelle.manager.model.customer.Customer;
import de.tankstelle.manager.model.tank.InsufficientFuelException;
import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.types.WorkshopServiceUpgrade;
import de.tankstelle.manager.model.upgrade.types.SupermarketServiceUpgrade;
import de.tankstelle.manager.model.upgrade.types.CarWashServiceUpgrade;
import java.util.List;
import java.util.function.Consumer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

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
            Random workshopRandom = new Random();
            for (Customer customer : customers) {
                if (customer.getFuelPreference() == type) {
                    double amount = customer.calculatePurchaseAmount();
                    boolean tookWorkshop = false;
                    double workshopRevenue = 0;
                    boolean tookSupermarket = false;
                    double supermarketRevenue = 0;
                    // NEU: Listen für gezogene Services
                    List<String> workshopServicesTaken = new java.util.ArrayList<>();
                    List<String> supermarketServicesTaken = new java.util.ArrayList<>();
                    // Waschstraße-Logik: Nach erfolgreichem Benzinkauf
                    List<Upgrade> upgrades = gameState.getInstalledUpgrades();
                    List<CarWashServiceUpgrade> carWashUpgrades = upgrades.stream()
                        .filter(u -> u instanceof CarWashServiceUpgrade)
                        .map(u -> (CarWashServiceUpgrade) u)
                        .filter(cw -> cw.getServiceRevenue() > 0.0)
                        .toList();
                    boolean tookCarWash = false;
                    double carWashRevenue = 0;
                    List<String> carWashServicesTaken = new java.util.ArrayList<>();
                    // Werkstatt-Logik: Nach erfolgreichem Benzinkauf
                    List<WorkshopServiceUpgrade> workshopUpgrades = upgrades.stream()
                        .filter(u -> u instanceof WorkshopServiceUpgrade)
                        .map(u -> (WorkshopServiceUpgrade) u)
                        .filter(ws -> ws.getServiceRevenue() > 0.0)
                        .toList();
                    List<SupermarketServiceUpgrade> supermarketUpgrades = upgrades.stream()
                        .filter(u -> u instanceof SupermarketServiceUpgrade)
                        .map(u -> (SupermarketServiceUpgrade) u)
                        .filter(sm -> sm.getServiceRevenue() > 0.0)
                        .toList();
                    // Benzinkauf-Logik wie gehabt
                    if (priceDelta <= 0.10) {
                        // Sehr zufrieden, kauft
                        try {
                            tank.dispenseFuel(amount);
                            double profit = (stationPrice - marketPrice) * amount;
                            double revenue = stationPrice * amount;
                            // Werkstatt: Für jede Dienstleistung 10%-Chance
                            for (WorkshopServiceUpgrade ws : workshopUpgrades) {
                                if (workshopRandom.nextDouble() < 0.10) {
                                    workshopRevenue += ws.getServiceRevenue();
                                    tookWorkshop = true;
                                    workshopServicesTaken.add(ws.getName() + String.format(": %.2f €", ws.getServiceRevenue()));
                                }
                            }
                            // Supermarkt: Für jede Dienstleistung 20%-Chance
                            for (SupermarketServiceUpgrade sm : supermarketUpgrades) {
                                if (workshopRandom.nextDouble() < 0.20) {
                                    supermarketRevenue += sm.getServiceRevenue();
                                    tookSupermarket = true;
                                    supermarketServicesTaken.add(sm.getName() + String.format(": %.2f €", sm.getServiceRevenue()));
                                }
                            }
                            // Waschstraße: Für jede Dienstleistung 10%-Chance
                            for (CarWashServiceUpgrade cw : carWashUpgrades) {
                                if (workshopRandom.nextDouble() < 0.10) {
                                    carWashRevenue += cw.getServiceRevenue();
                                    tookCarWash = true;
                                    carWashServicesTaken.add(cw.getName() + String.format(": %.2f €", cw.getServiceRevenue()));
                                }
                            }
                            stats.recordSale(type, amount, profit);
                            gameState.setCash(gameState.getCash() + revenue + workshopRevenue + supermarketRevenue + carWashRevenue);
                            gameState.addSatisfactionDelta(+0.01);
                            if (logConsumer != null) {
                                StringBuilder log = new StringBuilder(String.format("Kunde kaufte %.0f L %s für %.2f €", amount, typeToString(type), revenue));
                                if (tookWorkshop) {
                                    log.append(" und nahm Werkstattservice in Anspruch");
                                    if (!workshopServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", workshopServicesTaken)).append(")");
                                    }
                                }
                                if (tookSupermarket) {
                                    log.append(" und kaufte im Supermarkt ein");
                                    if (!supermarketServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", supermarketServicesTaken)).append(")");
                                    }
                                }
                                if (tookCarWash) {
                                    log.append(" und nutzte die Waschstraße");
                                    if (!carWashServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", carWashServicesTaken)).append(")");
                                    }
                                }
                                log.append(".");
                                logConsumer.accept(log.toString());
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
                            for (WorkshopServiceUpgrade ws : workshopUpgrades) {
                                if (workshopRandom.nextDouble() < 0.10) {
                                    workshopRevenue += ws.getServiceRevenue();
                                    tookWorkshop = true;
                                    workshopServicesTaken.add(ws.getName() + String.format(": %.2f €", ws.getServiceRevenue()));
                                }
                            }
                            for (SupermarketServiceUpgrade sm : supermarketUpgrades) {
                                if (workshopRandom.nextDouble() < 0.20) {
                                    supermarketRevenue += sm.getServiceRevenue();
                                    tookSupermarket = true;
                                    supermarketServicesTaken.add(sm.getName() + String.format(": %.2f €", sm.getServiceRevenue()));
                                }
                            }
                            for (CarWashServiceUpgrade cw : carWashUpgrades) {
                                if (workshopRandom.nextDouble() < 0.10) {
                                    carWashRevenue += cw.getServiceRevenue();
                                    tookCarWash = true;
                                    carWashServicesTaken.add(cw.getName() + String.format(": %.2f €", cw.getServiceRevenue()));
                                }
                            }
                            stats.recordSale(type, amount, profit);
                            gameState.setCash(gameState.getCash() + revenue + workshopRevenue + supermarketRevenue + carWashRevenue);
                            gameState.addSatisfactionDelta(+0.005);
                            if (logConsumer != null) {
                                StringBuilder log = new StringBuilder(String.format("Kunde kaufte %.0f L %s für %.2f €", amount, typeToString(type), revenue));
                                if (tookWorkshop) {
                                    log.append(" und nahm Werkstattservice in Anspruch");
                                    if (!workshopServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", workshopServicesTaken)).append(")");
                                    }
                                }
                                if (tookSupermarket) {
                                    log.append(" und kaufte im Supermarkt ein");
                                    if (!supermarketServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", supermarketServicesTaken)).append(")");
                                    }
                                }
                                if (tookCarWash) {
                                    log.append(" und nutzte die Waschstraße");
                                    if (!carWashServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", carWashServicesTaken)).append(")");
                                    }
                                }
                                log.append(".");
                                logConsumer.accept(log.toString());
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
                            for (WorkshopServiceUpgrade ws : workshopUpgrades) {
                                if (workshopRandom.nextDouble() < 0.10) {
                                    workshopRevenue += ws.getServiceRevenue();
                                    tookWorkshop = true;
                                    workshopServicesTaken.add(ws.getName() + String.format(": %.2f €", ws.getServiceRevenue()));
                                }
                            }
                            for (SupermarketServiceUpgrade sm : supermarketUpgrades) {
                                if (workshopRandom.nextDouble() < 0.20) {
                                    supermarketRevenue += sm.getServiceRevenue();
                                    tookSupermarket = true;
                                    supermarketServicesTaken.add(sm.getName() + String.format(": %.2f €", sm.getServiceRevenue()));
                                }
                            }
                            for (CarWashServiceUpgrade cw : carWashUpgrades) {
                                if (workshopRandom.nextDouble() < 0.20) {
                                    carWashRevenue += cw.getServiceRevenue();
                                    tookCarWash = true;
                                    carWashServicesTaken.add(cw.getName() + String.format(": %.2f €", cw.getServiceRevenue()));
                                }
                            }
                            stats.recordSale(type, amount, profit);
                            gameState.setCash(gameState.getCash() + revenue + workshopRevenue + supermarketRevenue + carWashRevenue);
                            gameState.addSatisfactionDelta(-0.02);
                            if (logConsumer != null) {
                                StringBuilder log = new StringBuilder(String.format("Kunde kaufte %.0f L %s für %.2f €", amount, typeToString(type), revenue));
                                if (tookWorkshop) {
                                    log.append(" und nahm Werkstattservice in Anspruch");
                                    if (!workshopServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", workshopServicesTaken)).append(")");
                                    }
                                }
                                if (tookSupermarket) {
                                    log.append(" und kaufte im Supermarkt ein");
                                    if (!supermarketServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", supermarketServicesTaken)).append(")");
                                    }
                                }
                                if (tookCarWash) {
                                    log.append(" und nutzte die Waschstraße");
                                    if (!carWashServicesTaken.isEmpty()) {
                                        log.append(" (").append(String.join(", ", carWashServicesTaken)).append(")");
                                    }
                                }
                                log.append(".");
                                logConsumer.accept(log.toString());
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
        // Automatisierungs-Logik: Für alle Tanks mit aktivierter Automatisierung
        for (FuelType type : FuelType.values()) {
            if (gameState.isAutomationEnabled(type)) {
                FuelTank tank = gameState.getTanks().get(type);
                double threshold = gameState.getAutomationThreshold(type);
                double fillPercent = tank.getCurrentLevel() / tank.getCapacity();
                if (fillPercent < threshold) {
                    double targetLevel = tank.getCapacity() * 0.8;
                    double toOrder = targetLevel - tank.getCurrentLevel();
                    if (toOrder > 0) {
                        double marketPrice = marketService.getCurrentMarketPrice(type);
                        double totalCost = toOrder * marketPrice;
                        if (gameState.getCash() >= totalCost) {
                            gameState.setCash(gameState.getCash() - totalCost);
                            tank.refill(toOrder);
                            if (logConsumer != null) {
                                logConsumer.accept("[AUTOMATISIERTE BESTELLUNG] " + String.format("%.0f L %s für %.2f € nachbestellt (auf 80%%)", toOrder, typeToString(type), totalCost));
                            }
                            gameState.notifyObservers();
                        } else {
                            if (logConsumer != null) {
                                logConsumer.accept("[AUTOMATISIERTE BESTELLUNG] Nicht genug Geld für automatische Bestellung von " + typeToString(type));
                            }
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