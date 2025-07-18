package de.tankstelle.manager.view.components;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PriceAutomationDialog extends Stage {
    private final CheckBox enableAuto;
    private final Slider marginSlider;
    private final Label marginLabel;
    private boolean resultEnabled;
    private double resultMargin;

    public PriceAutomationDialog(boolean initialEnabled, double initialMargin) {
        setTitle("Preisautomatisierung einstellen");
        initModality(Modality.APPLICATION_MODAL);
        setMinWidth(340);
        VBox root = new VBox(14);
        root.setPadding(new Insets(18));

        enableAuto = new CheckBox("Automatische Preisberechnung aktivieren");
        enableAuto.setSelected(initialEnabled);
        marginSlider = new Slider(0, 50, initialMargin);
        marginSlider.setShowTickLabels(true);
        marginSlider.setShowTickMarks(true);
        marginSlider.setMajorTickUnit(10);
        marginSlider.setMinorTickCount(1);
        marginSlider.setBlockIncrement(1);
        marginLabel = new Label(String.format("Marge: %.0f %%", initialMargin));
        marginSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            marginLabel.setText(String.format("Marge: %.0f %%", newVal.doubleValue()));
        });

        Button okBtn = new Button("OK");
        Button cancelBtn = new Button("Abbrechen");
        okBtn.setOnAction(e -> {
            resultEnabled = enableAuto.isSelected();
            resultMargin = marginSlider.getValue();
            close();
        });
        cancelBtn.setOnAction(e -> {
            resultEnabled = initialEnabled;
            resultMargin = initialMargin;
            close();
        });
        root.getChildren().addAll(enableAuto, marginSlider, marginLabel, new VBox(8, okBtn, cancelBtn));
        setScene(new Scene(root));
    }

    public boolean getResultEnabled() { return resultEnabled; }
    public double getResultMargin() { return resultMargin; }
} 