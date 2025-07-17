package de.tankstelle.manager.view.components;

import de.tankstelle.manager.model.fuel.FuelType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class DashboardComponent extends VBox {
    private final Label revenueLabel;
    private final Label profitLabel;
    private final GridPane salesGrid;
    private final Label[] salesLabels;
    private final ProgressBar satisfactionBar;
    private final Label satisfactionLabel;

    public DashboardComponent() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setPadding(new Insets(10, 0, 0, 0));

        revenueLabel = new Label("Umsatz: 0,00 €");
        revenueLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
        profitLabel = new Label("Gewinn: 0,00 €");
        profitLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");

        salesGrid = new GridPane();
        salesGrid.setHgap(18);
        salesGrid.setVgap(4);
        salesGrid.setAlignment(Pos.CENTER);
        salesLabels = new Label[FuelType.values().length];
        int col = 0;
        for (FuelType type : FuelType.values()) {
            Label l = new Label(typeToString(type) + ": 0 L");
            l.setStyle("-fx-font-size: 13;");
            salesLabels[type.ordinal()] = l;
            salesGrid.add(l, col++, 0);
        }

        satisfactionBar = new ProgressBar(1.0);
        satisfactionBar.setPrefWidth(220);
        satisfactionLabel = new Label("Kundenzufriedenheit: 100 % (Sehr zufrieden)");
        satisfactionLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");
        this.getChildren().addAll(revenueLabel, profitLabel, salesGrid, satisfactionLabel, satisfactionBar);
    }

    public void update(double revenue, double profit, Map<FuelType, Integer> salesVolume, double satisfaction) {
        revenueLabel.setText(String.format("Umsatz: %.2f €", revenue));
        profitLabel.setText(String.format("Gewinn: %.2f €", profit));
        for (FuelType type : FuelType.values()) {
            int vol = salesVolume.getOrDefault(type, 0);
            salesLabels[type.ordinal()].setText(typeToString(type) + ": " + vol + " L");
        }
        satisfactionBar.setProgress(satisfaction);
        String text = String.format("Kundenzufriedenheit: %.0f %%", satisfaction * 100);
        if (satisfaction > 0.85) text += " (Sehr zufrieden)";
        else if (satisfaction > 0.6) text += " (Zufrieden)";
        else if (satisfaction > 0.3) text += " (Unzufrieden)";
        else text += " (Sehr unzufrieden)";
        satisfactionLabel.setText(text);
    }

    private String typeToString(FuelType type) {
        switch (type) {
            case SUPER_95: return "Super 95";
            case SUPER_95_E10: return "Super 95 E10";
            case SUPER_PLUS: return "Super Plus";
            case DIESEL: return "Diesel";
            default: return type.name();
        }
    }
} 