package calories_calculator_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import calories_calculator.ActivityLevel;
import calories_calculator.CaloriesCalculator;
import calories_calculator.Gender;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class CaloriesCalculatorController {

    @FXML
    private TextField activityLevelField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField burnedCaloriesField;

    @FXML
    private Button calculateDailyCaloriesButton;

    @FXML
    private Button calculateMaxCaloriesButton;

    @FXML
    private TextField dailyCaloriesField;

    @FXML
    private TextField genderField;

    @FXML
    private TextField heightField;

    @FXML
    private Button loadButton;

    @FXML
    private TextField maxCaloriesField;

    @FXML
    private TextField weightField;

    private int parseInt(TextField field) {
        return Integer.parseInt(field.getText().trim());
    }

    private double parseDouble(TextField field) {
        return Double.parseDouble(field.getText().trim().replace(',', '.'));
    }

    private Gender parseGender() {
        int value = parseInt(genderField);

        if (value == 1) return Gender.MALE;
        if (value == 0) return Gender.FEMALE;

        throw new IllegalArgumentException("Полът трябва да е 0 или 1");
    }

    @FXML
    void calculateDailyCaloriesOnAction(ActionEvent event) {
        try {
            CaloriesCalculator calculator = createCalculator(0.0);

            double dailyCalories = calculator.calculateDailyBurnedCalories();
            dailyCaloriesField.setText(String.format("%.2f", dailyCalories));

        } catch (NumberFormatException e) {
            showWarning("Невалидни данни", "Моля попълнете всички полета с валидни числа.");
        } catch (IllegalArgumentException e) {
            showWarning("Невалидни данни", e.getMessage());
        }
    }

    @FXML
    void calculateMaxCaloriesOnAction(ActionEvent event) {
        try {
            double exerciseCalories = parseDouble(burnedCaloriesField);

            CaloriesCalculator calculator = createCalculator(exerciseCalories);

            double maxCalories = calculator.calculateCaloriesForWeightLoss();
            maxCaloriesField.setText(String.format("%.2f", maxCalories));

        } catch (NumberFormatException e) {
            showWarning("Невалидни данни", "Моля попълнете всички полета с валидни числа.");
        } catch (IllegalArgumentException e) {
            showWarning("Невалидни данни", e.getMessage());
        }
    }

    private CaloriesCalculator createCalculator(double exerciseCalories) {
        double weightKg = parseDouble(weightField);
        double heightCm = parseDouble(heightField);
        int age = parseInt(ageField);
        Gender gender = parseGender();

        int activityNumber = parseInt(activityLevelField);
        ActivityLevel activityLevel = ActivityLevel.userChoice(activityNumber);

        return new CaloriesCalculator(
                weightKg,
                heightCm,
                age,
                gender,
                activityLevel,
                exerciseCalories
        );
    }

    @FXML
    void loadButtonOnAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Избери текстов файл");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text files", "*.txt")
        );

        File file = chooser.showOpenDialog(loadButton.getScene().getWindow());
        if (file == null) return;

        boolean loadedSomething = false;

        Map<String, TextField> fields = Map.of(
                "gender", genderField,
                "height", heightField,
                "weight", weightField,
                "age", ageField,
                "activity", activityLevelField,
                "exercise", burnedCaloriesField
        );

        try {
            for (String line : Files.readAllLines(file.toPath())) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] data = line.split("=", 2);
                if (data.length != 2) continue;

                TextField field = fields.get(data[0].toLowerCase());
                if (field != null) {
                    field.setText(data[1].trim());
                    loadedSomething = true;
                }
            }

            if (!loadedSomething) {
                showWarning("Невалиден файл",
                        "Очакван формат:\n" +
                                "gender=1\nheight=175\nweight=70\nage=20");
            }

        } catch (IOException e) {
            showError("Грешка при четене на файла", e.getMessage());
        }
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}