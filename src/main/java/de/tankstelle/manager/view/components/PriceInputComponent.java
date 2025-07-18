package de.tankstelle.manager.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import de.tankstelle.manager.model.station.GameState;
import de.tankstelle.manager.model.fuel.FuelType;
import de.tankstelle.manager.model.upgrade.types.PriceAutomationUpgrade;
import java.util.function.Consumer;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;

public class PriceInputComponent extends VBox {
    private final Label fuelTypeLabel;
    private final TextField priceField;
    private final Label feedbackLabel;
    private final Button priceAutoBtn;

    public PriceInputComponent(String fuelType, double initialPrice) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(6);

        fuelTypeLabel = new Label(fuelType);
        fuelTypeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");

        priceField = new TextField(String.format("%.2f", initialPrice));
        priceField.setPrefWidth(60);
        priceField.setAlignment(Pos.CENTER_RIGHT);

        feedbackLabel = new Label("");
        feedbackLabel.setTextFill(Color.RED);
        feedbackLabel.setStyle("-fx-font-size: 11;");

        this.getChildren().addAll(fuelTypeLabel, priceField, feedbackLabel);

        // Preisautomatisierungs-Button (optional, erst nachträglich sichtbar)
        priceAutoBtn = new Button("Preisautomatisierung");
        priceAutoBtn.setVisible(false);
        priceAutoBtn.setManaged(false);
        this.getChildren().add(priceAutoBtn);

        // Dialog für Preisautomatisierung
        priceAutoBtn.setOnAction(e -> {
            if (this.priceAutoDialogCallback != null) this.priceAutoDialogCallback.run();
        });

        // Methoden für Main/OrderDialog, um Automatisierung zu aktivieren
        // enableAuto.setOnAction(e -> {
        //     if (this.automationCallback != null) this.automationCallback.accept(enableAuto.isSelected());
        // });
        // marginSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        //     if (this.marginCallback != null) this.marginCallback.accept(newVal.doubleValue());
        // });
    }

    // Für Main/OrderDialog: Automatisierung aktivieren und Marge setzen
    private Consumer<Boolean> automationCallback;
    private Consumer<Double> marginCallback;
    public void setAutomationCallback(Consumer<Boolean> cb) { this.automationCallback = cb; }
    public void setMarginCallback(Consumer<Double> cb) { this.marginCallback = cb; }
    public void setAutomationUI(GameState gameState, FuelType type, double marktpreis) {
        boolean hasUpgrade = gameState.getInstalledUpgrades().stream().anyMatch(u -> u instanceof PriceAutomationUpgrade pau && pau.getFuelType() == type);
        priceAutoBtn.setVisible(hasUpgrade);
        priceAutoBtn.setManaged(hasUpgrade);
        boolean autoActive = hasUpgrade && gameState.isPriceAutomationEnabled(type);
        priceField.setEditable(!autoActive);
        priceField.setDisable(autoActive);
        if (autoActive) {
            feedbackLabel.setText("Preis wird automatisch berechnet");
            feedbackLabel.setTextFill(javafx.scene.paint.Color.BLUE);
        } else {
            feedbackLabel.setText("");
        }
    }

    // Callback, der beim Klick auf den Button ausgeführt wird (z.B. um Dialog zu öffnen)
    private Runnable priceAutoDialogCallback;
    public void setPriceAutoDialogCallback(Runnable cb) { this.priceAutoDialogCallback = cb; }

    public String getPriceText() {
        return priceField.getText();
    }

    public void setFeedback(String message, boolean error) {
        feedbackLabel.setText(message);
        feedbackLabel.setTextFill(error ? Color.RED : Color.GREEN);
    }

    public void clearFeedback() {
        feedbackLabel.setText("");
    }

    public TextField getPriceField() {
        return priceField;
    }

    public void updateAutomatedPrice(double marktpreis, double margin) {
        if (!priceField.isEditable()) {
            priceField.setText(String.format("%.2f", marktpreis * (1 + margin / 100)));
        }
    }
} 