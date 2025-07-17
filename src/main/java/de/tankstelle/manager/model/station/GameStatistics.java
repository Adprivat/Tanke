package de.tankstelle.manager.model.station;

import de.tankstelle.manager.model.fuel.FuelType;
import java.util.*;

public class GameStatistics {
    private double totalRevenue;
    private double totalProfit;
    private Map<FuelType, Integer> salesVolume;
    private List<DailyReport> dailyReports;

    public GameStatistics() {
        this.totalRevenue = 0.0;
        this.totalProfit = 0.0;
        this.salesVolume = new HashMap<>();
        this.dailyReports = new ArrayList<>();
    }

    public void recordSale(FuelType type, double amount, double profit) {
        totalRevenue += amount;
        totalProfit += profit;
        salesVolume.put(type, salesVolume.getOrDefault(type, 0) + (int) amount);
    }

    public DailyReport generateDailyReport() {
        DailyReport report = new DailyReport(totalRevenue, totalProfit, new HashMap<>(salesVolume));
        dailyReports.add(report);
        return report;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public Map<FuelType, Integer> getSalesVolume() {
        return salesVolume;
    }

    public List<DailyReport> getDailyReports() {
        return dailyReports;
    }

    // Nested class für Tagesbericht
    public static class DailyReport {
        private final double revenue;
        private final double profit;
        private final Map<FuelType, Integer> salesVolume;

        public DailyReport(double revenue, double profit, Map<FuelType, Integer> salesVolume) {
            this.revenue = revenue;
            this.profit = profit;
            this.salesVolume = salesVolume;
        }

        public double getRevenue() {
            return revenue;
        }

        public double getProfit() {
            return profit;
        }

        public Map<FuelType, Integer> getSalesVolume() {
            return salesVolume;
        }
    }
} 