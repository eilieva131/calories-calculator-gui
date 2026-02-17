package calories_calculator_gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class CaloriesCalculatorPane extends AnchorPane {

    public CaloriesCalculatorPane() {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("CalculatorScene.fxml"))
            );
            getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
