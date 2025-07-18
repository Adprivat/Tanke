package de.tankstelle.manager.view.upgrade;

import de.tankstelle.manager.model.upgrade.Upgrade;
import de.tankstelle.manager.model.upgrade.UpgradeCategory;
import de.tankstelle.manager.service.upgrade.UpgradeShopService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

public class UpgradeShopDialog extends Stage {
    private final UpgradeShopService shopService;
    private final VBox upgradeListBox = new VBox(10);
    private final ComboBox<UpgradeCategory> categoryBox = new ComboBox<>();
    private final Label infoLabel = new Label();
    private final Runnable onUpgradeBought;

    public UpgradeShopDialog(UpgradeShopService shopService, Runnable onUpgradeBought) {
        this.shopService = shopService;
        this.onUpgradeBought = onUpgradeBought;
        setTitle("Upgrade-Shop");
        initModality(Modality.APPLICATION_MODAL);
        setMinWidth(500);
        setMinHeight(400);

        categoryBox.getItems().addAll(UpgradeCategory.values());
        categoryBox.getSelectionModel().selectFirst();
        categoryBox.setOnAction(e -> refreshList());

        VBox root = new VBox(16,
                new Label("Kategorie wählen:"),
                categoryBox,
                new Separator(),
                upgradeListBox,
                infoLabel
        );
        root.setPadding(new Insets(16));
        Scene scene = new Scene(root);
        setScene(scene);
        refreshList();
    }

    private void refreshList() {
        upgradeListBox.getChildren().clear();
        UpgradeCategory cat = categoryBox.getValue();
        List<Upgrade> upgrades = shopService.getAvailableUpgrades(cat);
        if (upgrades.isEmpty()) {
            upgradeListBox.getChildren().add(new Label("Keine Upgrades verfügbar."));
            return;
        }
        for (Upgrade upg : upgrades) {
            VBox card = new VBox(4);
            card.setPadding(new Insets(8));
            card.setStyle("-fx-border-color: #aaa; -fx-background-color: #f9f9f9;");
            Label name = new Label(upg.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
            Label desc = new Label(upg.getDescription());
            Label cost = new Label(String.format("Kosten: %.2f €", upg.getCost()));
            Button buyBtn = new Button("Kaufen");
            buyBtn.setOnAction(e -> {
                UpgradeShopService.PurchaseResult result = shopService.purchaseUpgrade(upg.getId());
                switch (result) {
                    case SUCCESS -> {
                        infoLabel.setText("Upgrade gekauft: " + upg.getName());
                        if (onUpgradeBought != null) onUpgradeBought.run();
                    }
                    case INSUFFICIENT_FUNDS -> infoLabel.setText("Nicht genug Geld für dieses Upgrade.");
                    case PREREQUISITES_NOT_MET -> infoLabel.setText("Voraussetzungen nicht erfüllt.");
                    case NOT_FOUND -> infoLabel.setText("Upgrade nicht gefunden.");
                }
                refreshList();
            });
            card.getChildren().addAll(name, desc, cost, buyBtn);
            upgradeListBox.getChildren().add(card);
        }
    }
} 