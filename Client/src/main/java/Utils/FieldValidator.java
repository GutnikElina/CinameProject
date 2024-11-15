package Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class FieldValidator {
    // Валидация текстового поля
    public static boolean validateTextField(TextField textField, String errorMessage, int minLength) {
        String text = textField.getText().trim();
        if (text.isEmpty() || text.length() < minLength) {
            textField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
            UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            return false;
        } else {
            textField.setStyle("");
            return true;
        }
    }
    // Валидация числового поля
    public static boolean validateNumericField(TextField textField, String errorMessage) {
        try {
            Integer.parseInt(textField.getText().trim());
            textField.setStyle("");
            return true;
        } catch (NumberFormatException e) {
            textField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
            UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
    }
    // Валидация положительного числа
    public static boolean validatePositiveNumericField(TextField textField, String errorMessage) {
        try {
            int value = Integer.parseInt(textField.getText().trim());
            if (value < 0) {
                textField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
                UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
                return false;
            }
            textField.setStyle("");
            return true;
        } catch (NumberFormatException e) {
            textField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
            UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
    }
    // Валидация URL
    public static boolean validateUrlField(TextField textField, String errorMessage) {
        String url = textField.getText().trim();
        if (url.isEmpty() || !url.matches("^(https?|ftp)://[^\s/$.?#].[^\s]*$")) {
            textField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
            UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            return false;
        } else {
            textField.setStyle("");
            return true;
        }
    }

}
