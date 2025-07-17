package de.tankstelle.manager.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PriceInputComponent extends VBox {
    private final Label fuelTypeLabel;
    private final TextField priceField;
    private final Label feedbackLabel;

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
    }

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
} 