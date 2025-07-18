package de.tankstelle.manager.service.pricing;

import de.tankstelle.manager.model.fuel.Fuel;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.service.market.MarketService;
import de.tankstelle.manager.util.exception.InvalidPriceException;

public class PricingService {
    private final MarketService marketService;

    public PricingService(MarketService marketService) {
        this.marketService = marketService;
    }

    public PricingRecommendation calculateOptimalPrice(Fuel fuel) {
        double marketPrice = marketService.getCurrentMarketPrice(fuel.getType());
        double recommended = marketPrice + 0.05; // Einfaches Beispiel: 5 Cent über Markt
        return new PricingRecommendation(recommended, "5 Cent über Marktpreis für optimale Marge");
    }

    public CustomerImpact analyzePriceImpact(Fuel fuel, double newPrice) {
        double marketPrice = marketService.getCurrentMarketPrice(fuel.getType());
        double diff = newPrice - marketPrice;
        String msg;
        double impact;
        if (diff < 0) {
            msg = "Sehr attraktiv für Kunden.";
            impact = 0.2;
        } else if (diff < 0.05) {
            msg = "Leicht über Markt, kaum Einfluss.";
            impact = 0.0;
        } else if (diff < 0.10) {
            msg = "Spürbar teurer, weniger Kunden.";
            impact = -0.1;
        } else {
            msg = "Deutlich zu teuer, viele Kunden bleiben weg.";
            impact = -0.3;
        }
        return new CustomerImpact(impact, msg);
    }

    public void updatePrice(Fuel fuel, double newPrice) throws InvalidPriceException {
        if (newPrice < 0) {
            throw new InvalidPriceException("Preis darf nicht negativ sein.");
        }
        // Mindestpreisprüfung entfernt: beliebig niedrige Preise sind erlaubt
        fuel.setBasePrice(newPrice);
    }
} 