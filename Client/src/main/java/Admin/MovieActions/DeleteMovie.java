package Admin.MovieActions;

import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.Map;

public class DeleteMovie {

    @FXML private TextField idField;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void deleteMovieAction() {
        if (!FieldValidator.validatePositiveNumericField(idField, "ID фильма должен быть положительным числом.")) {
            return;
        }

        try {
            String command = createDeleteCommand(idField.getText());
            String response = AppUtils.sendJsonCommandToServer(command);

            if (response.contains("MOVIE_DELETED")) {
                UIUtils.showAlert("Успех", "Фильм успешно удален.", Alert.AlertType.INFORMATION);
                handleBackButton();
            } else if (response.contains("ERROR")) {
                UIUtils.showAlert("Ошибка", "Не удалось удалить фильм: " + response, Alert.AlertType.ERROR);
            }
        } catch (IllegalArgumentException e) {
            UIUtils.showAlert("Ошибка", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Произошла ошибка при соединении с сервером: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String createDeleteCommand(String hallId) {
        try {
            int movieId = Integer.parseInt(hallId.trim());
            return gson.toJson(Map.of(
                    "command", "MOVIE;DELETE",
                    "data", Map.of("id", movieId)
            ));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID фильма должен быть числом.");
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}