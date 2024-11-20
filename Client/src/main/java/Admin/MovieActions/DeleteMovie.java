package Admin.MovieActions;

import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DeleteMovie {

    @FXML private TextField idField;
    @FXML private Button backButton;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void deleteMovieAction(ActionEvent event) {
        if (!FieldValidator.validatePositiveNumericField(idField, "ID фильма должен быть положительным числом.")) {
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            int movieId = Integer.parseInt(idField.getText());

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("command", "MOVIE;DELETE;");
            requestData.put("data", Map.of("id", movieId));

            String jsonRequest = objectMapper.writeValueAsString(requestData);
            out.println(jsonRequest);

            String response = in.readLine();
            if (response.contains("MOVIE_DELETED")) {
                UIUtils.showAlert("Фильм удален", "Фильм был успешно удален.", Alert.AlertType.INFORMATION);
                closeStage();
            } else if (response.contains("MOVIE_NOT_FOUND")) {
                UIUtils.showAlert("Ошибка", "Фильм с таким ID не найден.", Alert.AlertType.ERROR);
            } else if (response.startsWith("ERROR")) {
                UIUtils.showAlert("Ошибка", response.split(";", 2)[1], Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при соединении с сервером: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}
