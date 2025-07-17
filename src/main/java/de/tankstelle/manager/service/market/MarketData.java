package de.tankstelle.manager.service.market;

public class MarketData {
    private double price;
    private double fluctuation;

    public MarketData(double price, double fluctuation) {
        this.price = price;
        this.fluctuation = fluctuation;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getFluctuation() {
        return fluctuation;
    }

    public void setFluctuation(double fluctuation) {
        this.fluctuation = fluctuation;
    }
} 