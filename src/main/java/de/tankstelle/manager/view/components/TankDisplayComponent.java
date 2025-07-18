package de.tankstelle.manager.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import java.util.function.BiConsumer;
import javafx.scene.layout.HBox;

public class TankDisplayComponent extends VBox {
    private final Label fuelTypeLabel;
    private final Rectangle fillRect;
    private final Rectangle backgroundRect;
    private final Label percentLabel;
    private final Label warningLabel;
    private final Label capacityLabel;
    private double capacity = 0.0;
    private final double width = 60;
    private final double height = 200;

    // Callback für Main: (TankDisplayComponent, Prozentwert)
    private BiConsumer<TankDisplayComponent, Double> shortcutCallback;
    public void setShortcutCallback(BiConsumer<TankDisplayComponent, Double> cb) { this.shortcutCallback = cb; }

    public TankDisplayComponent(String fuelType) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(8);

        fuelTypeLabel = new Label(fuelType);
        fuelTypeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        backgroundRect = new Rectangle(width, height, Color.LIGHTGRAY);
        backgroundRect.setArcWidth(12);
        backgroundRect.setArcHeight(12);
        fillRect = new Rectangle(width, 0, Color.LIMEGREEN);
        fillRect.setArcWidth(12);
        fillRect.setArcHeight(12);

        Pane fillPane = new Pane();
        fillPane.setPrefSize(width, height);
        fillPane.setMinSize(width, height);
        fillPane.setMaxSize(width, height);
        fillRect.setY(height); // Start: leer
        fillPane.getChildren().add(fillRect);

        percentLabel = new Label("0 %");
        percentLabel.setStyle("-fx-font-size: 13;");
        percentLabel.setTextFill(Color.BLACK);
        percentLabel.setMouseTransparent(true);

        StackPane barPane = new StackPane(backgroundRect, fillPane, percentLabel);
        barPane.setPrefSize(width, height);
        barPane.setMaxSize(width, height);
        barPane.setMinSize(width, height);
        barPane.setAlignment(Pos.BOTTOM_CENTER);

        warningLabel = new Label("");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12;");

        capacityLabel = new Label("");
        capacityLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555;");

        this.getChildren().addAll(fuelTypeLabel, barPane, warningLabel, capacityLabel);

        // Shortcut-Buttons für Direktkauf
        HBox shortcutBox = new HBox(16);
        shortcutBox.setAlignment(Pos.CENTER);
        Button btn25 = new Button("+25% füllen");
        btn25.setStyle("-fx-font-size: 10; -fx-background-color: #e0eaff; -fx-font-weight: bold; -fx-padding: 2 6 2 6;");
        Button btn50 = new Button("+50% füllen");
        btn50.setStyle("-fx-font-size: 10; -fx-background-color: #d0ffd0; -fx-font-weight: bold; -fx-padding: 2 6 2 6;");
        shortcutBox.getChildren().addAll(btn25, btn50);
        this.getChildren().remove(shortcutBox);
        this.getChildren().add(shortcutBox);

        btn25.setOnAction(e -> { if (this.shortcutCallback != null) shortcutCallback.accept(this, 0.25); });
        btn50.setOnAction(e -> { if (this.shortcutCallback != null) shortcutCallback.accept(this, 0.50); });
    }

    /**
     * Setzt den Füllstand (0.0 bis 1.0) und aktualisiert Anzeige und Warnung.
     */
    public void setFillLevel(double percent) {
        percent = Math.max(0, Math.min(1, percent));
        double fillHeight = percent * height;
        fillRect.setHeight(fillHeight);
        fillRect.setY(height - fillHeight); // immer von unten wachsen
        percentLabel.setText(String.format("%.0f %%", percent * 100));
        if (percent == 0) {
            fillRect.setFill(Color.DARKGRAY);
            warningLabel.setText("Leer!");
        } else if (percent < 0.2) {
            fillRect.setFill(Color.ORANGE);
            warningLabel.setText("Niedriger Stand!");
        } else {
            fillRect.setFill(Color.LIMEGREEN);
            warningLabel.setText("");
        }
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
        capacityLabel.setText(String.format("Kapazität: %.0f L", capacity));
    }
} 