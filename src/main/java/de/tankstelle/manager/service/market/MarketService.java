package de.tankstelle.manager.service.market;

import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.util.exception.InsufficientFundsException;
import de.tankstelle.manager.util.exception.MarketUnavailableException;

import java.util.*;

public class MarketService {
    private final Map<FuelType, MarketData> marketPrices = new EnumMap<>(FuelType.class);
    private final Random random = new Random();

    public MarketService() {
        // Initialpreise und Fluktuation
        marketPrices.put(FuelType.SUPER_95, new MarketData(1.80, 0.03));
        marketPrices.put(FuelType.SUPER_95_E10, new MarketData(1.75, 0.03));
        marketPrices.put(FuelType.SUPER_PLUS, new MarketData(1.95, 0.04));
        marketPrices.put(FuelType.DIESEL, new MarketData(1.65, 0.02));
    }

    public double getCurrentMarketPrice(FuelType fuelType) {
        return marketPrices.get(fuelType).getPrice();
    }

    public void updateMarketPrices() {
        for (MarketData data : marketPrices.values()) {
            double fluctuation = (random.nextDouble() - 0.5) * 2 * data.getFluctuation();
            double newPrice = Math.max(1.0, data.getPrice() + fluctuation);
            data.setPrice(newPrice);
        }
    }

    public FuelOrder placeFuelOrder(FuelType type, double amount, double availableCash) throws InsufficientFundsException, MarketUnavailableException {
        MarketData data = marketPrices.get(type);
        if (data == null) {
            throw new MarketUnavailableException("Marktdaten für " + type + " nicht verfügbar.");
        }
        double pricePerUnit = data.getPrice();
        double totalCost = pricePerUnit * amount;
        if (totalCost > availableCash) {
            throw new InsufficientFundsException("Nicht genügend Geld für die Bestellung.");
        }
        int deliveryTime = 5 + random.nextInt(11); // 5-15 Minuten
        return new FuelOrder(type, amount, pricePerUnit, totalCost, deliveryTime);
    }
} 