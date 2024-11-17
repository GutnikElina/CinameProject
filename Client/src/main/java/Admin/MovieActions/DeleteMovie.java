package Admin.MovieActions;

import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DeleteMovie {

    @FXML
    private TextField idField;
    @FXML
    private Button backButton;

    @FXML
    private void deleteMovieAction(ActionEvent event) {
        // Проверка ввода
        if (!FieldValidator.validatePositiveNumericField(idField, "ID фильма должен быть положительным числом.")) {
            return;
        }

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            int movieId = Integer.parseInt(idField.getText());
            String command = "MOVIE;DELETE;" + movieId;
            out.println(command);

            String response = in.readLine();
            if ("MOVIE_DELETED".equals(response)) {
                UIUtils.showAlert("Фильм удален", "Фильм был успешно удален.", Alert.AlertType.INFORMATION);
                closeStage();
            } else if ("MOVIE_NOT_FOUND".equals(response)) {
                UIUtils.showAlert("Ошибка", "Фильм с таким ID не найден.", Alert.AlertType.ERROR);
            } else if (response.startsWith("ERROR")) {
                UIUtils.showAlert("Ошибка", response.split(";", 2)[1], Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при соединении с сервером.", Alert.AlertType.ERROR);
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
