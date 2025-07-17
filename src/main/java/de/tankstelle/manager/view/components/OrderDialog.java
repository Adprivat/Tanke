package de.tankstelle.manager.view.components;

import de.tankstelle.manager.model.fuel.FuelType;
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

    public OrderDialog(Stage owner, QuadFunction<FuelType, Double, Double, Double, Void> onOrder, FuelType[] types, double[] marketPrices, double[] maxCapacities, double[] currentLevels) {
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
        VBox vbox = new VBox(10, grid, warningLabel, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        this.setScene(new Scene(vbox));
        this.setWidth(350);
        this.setHeight(300);

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

    // Hilfs-Interface für vier Parameter (da Java kein QuadFunction hat)
    @FunctionalInterface
    public interface QuadFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
} 