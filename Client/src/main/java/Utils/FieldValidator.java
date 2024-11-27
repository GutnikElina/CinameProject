package Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    // Валидация даты (например, yyyy-MM-dd)
    public static boolean validateDatePicker(DatePicker datePicker, String errorMessage) {
        LocalDate date = datePicker.getValue();
        if (date == null) {
            datePicker.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
            UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            return false;
        } else {
            datePicker.setStyle("");
            return true;
        }
    }

    // Валидация времени (например, HH:mm)
    public static boolean validateTime(TextField textField, String errorMessage) {
        String time = textField.getText().trim();
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            textField.setStyle("");
            return true;
        } catch (DateTimeParseException e) {
            textField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
            UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            return false;
        }
    }

    // Валидация ComboBox (проверка, что выбран элемент)
    public static <T> boolean validateComboBox(ComboBox<T> comboBox, String errorMessage) {
        T selectedValue = comboBox.getValue();
        if (selectedValue == null) {
            comboBox.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
            UIUtils.showAlert("Ошибка", errorMessage, Alert.AlertType.ERROR);
            return false;
        } else {
            comboBox.setStyle("");
            return true;
        }
    }

}
