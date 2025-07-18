package de.tankstelle.manager.view.components;

import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.types.OrderAutomationUpgrade;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OrderDialog extends Stage {
    private final ComboBox<FuelType> fuelTypeBox;
    private final TextField amountField;
    private final Label priceLabel;
    private final Label totalLabel;
    private final Label warningLabel;
    private final Button orderButton;
    private final Button closeButton;
    private QuadFunction<FuelType, Double, Double, Double, Void> onOrder;
    private double marketPrice = 0.0;
    private double maxCapacity = 0.0;
    private double currentLevel = 0.0;
    private FuelType orderedType = null;
    private double deliveredAmount = 0.0;
    private double totalCost = 0.0;

    public OrderDialog(Stage owner, QuadFunction<FuelType, Double, Double, Double, Void> onOrder, FuelType[] types, double[] marketPrices, double[] maxCapacities, double[] currentLevels, GameState gameState) {
        this.onOrder = onOrder;
        this.initOwner(owner);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Kraftstoff bestellen");

        fuelTypeBox = new ComboBox<>();
        fuelTypeBox.getItems().addAll(types);
        fuelTypeBox.getSelectionModel().selectFirst();

        amountField = new TextField("100");
        amountField.setPrefWidth(80);
        priceLabel = new Label();
        totalLabel = new Label();
        warningLabel = new Label("");
        warningLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 12;");
        orderButton = new Button("Bestellen");
        closeButton = new Button("Schließen");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Kraftstoffart:"), 0, 0);
        grid.add(fuelTypeBox, 1, 0);
        grid.add(new Label("Menge (L):"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Marktpreis (€/L):"), 0, 2);
        grid.add(priceLabel, 1, 2);
        grid.add(new Label("Gesamtkosten (€):"), 0, 3);
        grid.add(totalLabel, 1, 3);

        HBox buttonBox = new HBox(10, orderButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER);
        // Automatisierung-UI (nur wenn Upgrade gekauft)
        VBox automationBox = new VBox(6);
        automationBox.setPadding(new Insets(8, 0, 0, 0));
        automationBox.setStyle("-fx-border-color: #bbb; -fx-border-radius: 4; -fx-background-color: #f6f6f6;");
        Label autoTitle = new Label("Automatisierung");
        autoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        CheckBox enableAuto = new CheckBox("Automatische Bestellung aktivieren");
        Slider thresholdSlider = new Slider(0.05, 0.5, 0.2);
        thresholdSlider.setShowTickLabels(true);
        thresholdSlider.setShowTickMarks(true);
        thresholdSlider.setMajorTickUnit(0.1);
        thresholdSlider.setMinorTickCount(1);
        thresholdSlider.setBlockIncrement(0.01);
        Label thresholdLabel = new Label("Schwellenwert: 20 %");
        thresholdSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            thresholdLabel.setText(String.format("Schwellenwert: %.0f %%", newVal.doubleValue() * 100));
        });
        automationBox.getChildren().addAll(autoTitle, enableAuto, thresholdSlider, thresholdLabel);

        // Sichtbarkeit und Initialwerte je nach Upgrade
        fuelTypeBox.setOnAction(e -> updateAutomationUI(gameState, automationBox, enableAuto, thresholdSlider, thresholdLabel));
        // Initial setzen
        updateAutomationUI(gameState, automationBox, enableAuto, thresholdSlider, thresholdLabel);

        enableAuto.setOnAction(e -> {
            FuelType type = fuelTypeBox.getSelectionModel().getSelectedItem();
            gameState.setAutomationEnabled(type, enableAuto.isSelected());
        });
        thresholdSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            FuelType type = fuelTypeBox.getSelectionModel().getSelectedItem();
            gameState.setAutomationThreshold(type, newVal.doubleValue());
        });

        VBox vbox = new VBox(10, grid, warningLabel, automationBox, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        this.setScene(new Scene(vbox));
        this.setMinWidth(350);

        // Initialwerte setzen
        updateMarketInfo(types, marketPrices, maxCapacities, currentLevels);
        fuelTypeBox.setOnAction(e -> updateMarketInfo(types, marketPrices, maxCapacities, currentLevels));
        amountField.textProperty().addListener((obs, oldVal, newVal) -> updateTotal());

        orderButton.setOnAction(e -> {
            double amount = parseAmount();
            double freeSpace = maxCapacity - currentLevel;
            if (freeSpace <= 0) {
                warningLabel.setText("Fehler: Tank ist voll, keine Bestellung möglich!");
                return;
            }
            double delivered = Math.min(amount, freeSpace);
            double total = amount * marketPrice;
            orderedType = fuelTypeBox.getSelectionModel().getSelectedItem();
            deliveredAmount = delivered;
            totalCost = total;
            if (amount > freeSpace) {
                warningLabel.setText("Warnung: Lieferung konnte nur teilweise zugestellt werden! Sie zahlen trotzdem " + String.format("%.2f", total) + " €.");
            } else {
                warningLabel.setText("");
            }
            if (onOrder != null && orderedType != null && totalCost > 0) {
                onOrder.apply(orderedType, amount, delivered, total);
            }
            amountField.setText("100");
        });
        closeButton.setOnAction(e -> this.close());
        updateTotal();
    }

    public void updateMarketInfo(FuelType[] types, double[] marketPrices, double[] maxCapacities, double[] currentLevels) {
        int idx = fuelTypeBox.getSelectionModel().getSelectedIndex();
        marketPrice = marketPrices[idx];
        maxCapacity = maxCapacities[idx];
        currentLevel = currentLevels[idx];
        priceLabel.setText(String.format("%.2f", marketPrice));
        updateTotal();
    }

    private void updateTotal() {
        double amount = parseAmount();
        double total = amount * marketPrice;
        totalLabel.setText(String.format("%.2f", total));
    }

    public double parseAmount() {
        try {
            return Math.max(0, Double.parseDouble(amountField.getText().replace(",", ".")));
        } catch (Exception e) {
            return 0;
        }
    }

    public FuelType getSelectedFuelType() {
        return fuelTypeBox.getSelectionModel().getSelectedItem();
    }

    public FuelType getOrderedType() { return orderedType; }
    public double getDeliveredAmount() { return deliveredAmount; }
    public double getTotalCost() { return totalCost; }

    private void updateAutomationUI(GameState gameState, VBox automationBox, CheckBox enableAuto, Slider thresholdSlider, Label thresholdLabel) {
        FuelType type = fuelTypeBox.getSelectionModel().getSelectedItem();
        boolean hasUpgrade = gameState.getInstalledUpgrades().stream().anyMatch(u -> u instanceof OrderAutomationUpgrade oau && oau.getFuelType() == type);
        automationBox.setVisible(hasUpgrade);
        automationBox.setManaged(hasUpgrade);
        if (hasUpgrade) {
            enableAuto.setSelected(gameState.isAutomationEnabled(type));
            double thresh = gameState.getAutomationThreshold(type);
            thresholdSlider.setValue(thresh);
            thresholdLabel.setText(String.format("Schwellenwert: %.0f %%", thresh * 100));
        }
    }

    // Hilfs-Interface für vier Parameter (da Java kein QuadFunction hat)
    @FunctionalInterface
    public interface QuadFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
} 