package de.tankstelle.manager.view.components;

import de.tankstelle.manager.model.station.GameState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StatisticsWindow extends Stage {
    private final DashboardComponent dashboard;

    public StatisticsWindow(GameState gameState) {
        this.setTitle("Statistik & Kennzahlen");
        this.initModality(Modality.NONE);
        dashboard = new DashboardComponent();
        VBox root = new VBox(18, dashboard);
        root.setStyle("-fx-padding: 24;");
        this.setScene(new Scene(root, 480, 220));
        // Initiales Update
        dashboard.update(
            gameState.getStatistics().getTotalRevenue(),
            gameState.getStatistics().getTotalProfit(),
            gameState.getStatistics().getSalesVolume(),
            gameState.getCustomerSatisfaction()
        );
        // GameState-Änderungen übernehmen
        gameState.addObserver(state -> Platform.runLater(() -> dashboard.update(
            state.getStatistics().getTotalRevenue(),
            state.getStatistics().getTotalProfit(),
            state.getStatistics().getSalesVolume(),
            state.getCustomerSatisfaction()
        )));
    }
} 