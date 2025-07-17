package de.tankstelle.manager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import de.tankstelle.manager.view.components.TankDisplayComponent;
import de.tankstelle.manager.view.components.PriceInputComponent;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.fuel.Super95;
import de.tankstelle.manager.model.fuel.Super95E10;
import de.tankstelle.manager.model.fuel.SuperPlus;
import de.tankstelle.manager.model.fuel.Diesel;
import de.tankstelle.manager.model.tank.FuelTank;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.station.GameStatistics;
import de.tankstelle.manager.service.pricing.PricingService;
import de.tankstelle.manager.service.market.MarketService;
import java.util.EnumMap;
import java.util.Map;
import de.tankstelle.manager.model.station.GameStateObserver;
import de.tankstelle.manager.view.components.DashboardComponent;
import de.tankstelle.manager.view.components.OrderDialog;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import de.tankstelle.manager.service.simulation.SimulationService;
import de.tankstelle.manager.service.simulation.CustomerSimulationService;
import de.tankstelle.manager.service.simulation.CustomerGenerator;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import de.tankstelle.manager.view.components.StatisticsWindow;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        HBox tankBox = new HBox(24);
        tankBox.setAlignment(Pos.CENTER);

        TankDisplayComponent super95 = new TankDisplayComponent("Super 95");
        TankDisplayComponent super95e10 = new TankDisplayComponent("Super 95 E10");
        TankDisplayComponent superPlus = new TankDisplayComponent("Super Plus");
        TankDisplayComponent diesel = new TankDisplayComponent("Diesel");

        // Testwerte für die Füllstände
        super95.setFillLevel(0.75);
        super95e10.setFillLevel(0.15);
        superPlus.setFillLevel(0.0);
        diesel.setFillLevel(0.45);

        tankBox.getChildren().addAll(super95, super95e10, superPlus, diesel);

        // Preissteuerung unterhalb der Tankanzeigen
        HBox priceBox = new HBox(24);
        priceBox.setAlignment(Pos.CENTER);
        PriceInputComponent priceSuper95 = new PriceInputComponent("Super 95", 1.85);
        PriceInputComponent priceSuper95e10 = new PriceInputComponent("Super 95 E10", 1.78);
        PriceInputComponent priceSuperPlus = new PriceInputComponent("Super Plus", 2.02);
        PriceInputComponent priceDiesel = new PriceInputComponent("Diesel", 1.69);
        priceBox.getChildren().addAll(priceSuper95, priceSuper95e10, priceSuperPlus, priceDiesel);
        DashboardComponent dashboard = new DashboardComponent();
        Button orderButton = new Button("Kraftstoff bestellen");
        

        // Geld-Anzeige oben rechts
        Label cashLabel = new Label();
        cashLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1a7f37;");
        HBox cashBox = new HBox(cashLabel);
        cashBox.setAlignment(Pos.CENTER_RIGHT);
        cashBox.setPadding(new Insets(16, 32, 0, 0));
        root.setTop(cashBox);

        // Menüband mit Statistik-Button
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Menü");
        MenuItem statsItem = new MenuItem("Statistik anzeigen");
        menu.getItems().add(statsItem);
        menuBar.getMenus().add(menu);
        root.setTop(menuBar);

        // Spiel-Log
        TextArea gameLog = new TextArea();
        gameLog.setEditable(false);
        gameLog.setPrefRowCount(8);
        gameLog.setWrapText(true);
        gameLog.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");

        // --- Spiellogik-Objekte ---
        MarketService marketService = new MarketService();
        PricingService pricingService = new PricingService(marketService);
        Map<FuelType, FuelTank> tanks = new EnumMap<>(FuelType.class);
        tanks.put(FuelType.SUPER_95, new FuelTank(new Super95(1.85), 1000, 750));
        tanks.put(FuelType.SUPER_95_E10, new FuelTank(new Super95E10(1.78), 1000, 150));
        tanks.put(FuelType.SUPER_PLUS, new FuelTank(new SuperPlus(2.02), 1000, 0));
        tanks.put(FuelType.DIESEL, new FuelTank(new Diesel(1.69), 1000, 450));
        Map<FuelType, Double> prices = new EnumMap<>(FuelType.class);
        prices.put(FuelType.SUPER_95, 1.85);
        prices.put(FuelType.SUPER_95_E10, 1.78);
        prices.put(FuelType.SUPER_PLUS, 2.02);
        prices.put(FuelType.DIESEL, 1.69);
        GameState gameState = new GameState(10000, tanks, prices, new GameStatistics());

        // Preisvalidierung und Feedback
        priceSuper95.getPriceField().textProperty().addListener((obs, oldVal, newVal) -> {
            validatePriceInput(priceSuper95, 1.50);
        });
        priceSuper95e10.getPriceField().textProperty().addListener((obs, oldVal, newVal) -> {
            validatePriceInput(priceSuper95e10, 1.45);
        });
        priceSuperPlus.getPriceField().textProperty().addListener((obs, oldVal, newVal) -> {
            validatePriceInput(priceSuperPlus, 1.70);
        });
        priceDiesel.getPriceField().textProperty().addListener((obs, oldVal, newVal) -> {
            validatePriceInput(priceDiesel, 1.30);
        });

        // Preisänderung an Spiellogik übergeben
        priceSuper95.getPriceField().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) applyPriceChange(priceSuper95, FuelType.SUPER_95, gameState, pricingService);
        });
        priceSuper95e10.getPriceField().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) applyPriceChange(priceSuper95e10, FuelType.SUPER_95_E10, gameState, pricingService);
        });
        priceSuperPlus.getPriceField().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) applyPriceChange(priceSuperPlus, FuelType.SUPER_PLUS, gameState, pricingService);
        });
        priceDiesel.getPriceField().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) applyPriceChange(priceDiesel, FuelType.DIESEL, gameState, pricingService);
        });

        // Tankanzeigen dynamisch an GameState koppeln
        gameState.addObserver(new GameStateObserver() {
            @Override
            public void onGameStateChanged(GameState state) {
                Platform.runLater(() -> {
                    super95.setFillLevel(state.getTanks().get(FuelType.SUPER_95).getFillPercentage());
                    super95e10.setFillLevel(state.getTanks().get(FuelType.SUPER_95_E10).getFillPercentage());
                    superPlus.setFillLevel(state.getTanks().get(FuelType.SUPER_PLUS).getFillPercentage());
                    diesel.setFillLevel(state.getTanks().get(FuelType.DIESEL).getFillPercentage());
                    dashboard.update(
                        state.getStatistics().getTotalRevenue(),
                        state.getStatistics().getTotalProfit(),
                        state.getStatistics().getSalesVolume(),
                        state.getCustomerSatisfaction()
                    );
                    cashLabel.setText(String.format("Guthaben: %.2f €", state.getCash()));
                });
            }
        });
        // Initiales Update
        super95.setFillLevel(gameState.getTanks().get(FuelType.SUPER_95).getFillPercentage());
        super95e10.setFillLevel(gameState.getTanks().get(FuelType.SUPER_95_E10).getFillPercentage());
        superPlus.setFillLevel(gameState.getTanks().get(FuelType.SUPER_PLUS).getFillPercentage());
        diesel.setFillLevel(gameState.getTanks().get(FuelType.DIESEL).getFillPercentage());
        // Initiales Dashboard- und Geld-Update
        dashboard.update(
            gameState.getStatistics().getTotalRevenue(),
            gameState.getStatistics().getTotalProfit(),
            gameState.getStatistics().getSalesVolume(),
            gameState.getCustomerSatisfaction()
        );
        cashLabel.setText(String.format("Guthaben: %.2f €", gameState.getCash()));

        orderButton.setOnAction(e -> {
            FuelType[] types = FuelType.values();
            double[] marketPrices = new double[types.length];
            double[] maxCaps = new double[types.length];
            double[] levels = new double[types.length];
            for (int i = 0; i < types.length; i++) {
                marketPrices[i] = marketService.getCurrentMarketPrice(types[i]);
                maxCaps[i] = gameState.getTanks().get(types[i]).getCapacity();
                levels[i] = gameState.getTanks().get(types[i]).getCurrentLevel();
            }
            OrderDialog dialog = new OrderDialog(
                ((Stage) orderButton.getScene().getWindow()),
                (delivered, totalCost) -> {}, // Callback nicht mehr genutzt
                types, marketPrices, maxCaps, levels
            );
            dialog.showAndWait();
            FuelType type = dialog.getOrderedType();
            double deliveredAmount = dialog.getDeliveredAmount();
            double totalCost = dialog.getTotalCost();
            if (type != null && dialog.getTotalCost() > 0) {
                gameState.setCash(gameState.getCash() - dialog.getTotalCost());
                gameState.getTanks().get(type).refill(dialog.getDeliveredAmount());
                gameState.notifyObservers();
                double orderedAmount = dialog.parseAmount();
                double deliveredOrderedAmount = dialog.getDeliveredAmount();
                if (deliveredOrderedAmount < orderedAmount) {
                    showInfo("Bestellung abgeschlossen!\nAchtung: Es konnten nur " + String.format("%.0f", deliveredOrderedAmount) + " L geliefert werden. Sie haben trotzdem den vollen Preis bezahlt.");
                } else {
                    showInfo("Bestellung erfolgreich! Geliefert: " + String.format("%.0f", deliveredOrderedAmount) + " L.");
                }
            }
        });

        // Tankkapazität unter Balken anzeigen
        super95.setCapacity(gameState.getTanks().get(FuelType.SUPER_95).getCapacity());
        super95e10.setCapacity(gameState.getTanks().get(FuelType.SUPER_95_E10).getCapacity());
        superPlus.setCapacity(gameState.getTanks().get(FuelType.SUPER_PLUS).getCapacity());
        diesel.setCapacity(gameState.getTanks().get(FuelType.DIESEL).getCapacity());

        // --- SimulationService vorbereiten ---
        CustomerSimulationService customerSimService = new CustomerSimulationService();
        SimulationService simulationService = new SimulationService(gameState, customerSimService, marketService);
        Button simButton = new Button("Tankstelle öffnen");
        simButton.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-padding: 8 24 8 24;");
        VBox centerBox = new VBox(32, tankBox, priceBox, gameLog, orderButton, simButton);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        simButton.setOnAction(e -> {
            if (simButton.getText().equals("Tankstelle öffnen")) {
                simulationService.startSimulation();
                simButton.setText("Tankstelle schließen");
            } else {
                simulationService.pauseSimulation();
                simButton.setText("Tankstelle öffnen");
            }
        });

        simulationService.setLogConsumer(msg -> Platform.runLater(() -> {
            if (gameLog.getText().length() > 4000) gameLog.clear();
            gameLog.appendText(msg + "\n");
            gameLog.setScrollTop(Double.MAX_VALUE);
        }));

        statsItem.setOnAction(e -> {
            StatisticsWindow statsWin = new StatisticsWindow(gameState);
            statsWin.show();
        });

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Tankstellen-Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Prüft Preisfeld und gibt Feedback (z.B. zu niedrig, negativ, Formatfehler).
     */
    private void validatePriceInput(PriceInputComponent comp, double minPrice) {
        String text = comp.getPriceText().replace(",", ".");
        try {
            double value = Double.parseDouble(text);
            if (value < 0) {
                comp.setFeedback("Preis darf nicht negativ sein!", true);
            } else if (value < minPrice) {
                comp.setFeedback("Preis zu niedrig!", true);
            } else {
                comp.setFeedback("Preis gültig", false);
            }
        } catch (NumberFormatException e) {
            if (text.isEmpty()) {
                comp.clearFeedback();
            } else {
                comp.setFeedback("Ungültiges Format!", true);
            }
        }
    }

    /**
     * Übernimmt Preisänderung in GameState und PricingService, wenn gültig.
     */
    private void applyPriceChange(PriceInputComponent comp, FuelType type, GameState gameState, PricingService pricingService) {
        String text = comp.getPriceText().replace(",", ".");
        try {
            double value = Double.parseDouble(text);
            if (value > 0 && !comp.getPriceField().getStyleClass().contains("error")) {
                pricingService.updatePrice(gameState.getTanks().get(type).getFuelType(), value);
                gameState.getCurrentPrices().put(type, value);
                comp.setFeedback("Preis übernommen", false);
            }
        } catch (Exception e) {
            // Fehler ignorieren, Feedback bleibt
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Bestellung");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 