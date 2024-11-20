package Admin.SessionActions;

import Utils.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FindSession {
    @FXML
    public Button backButton;
    @FXML
    private TextField idField;
    @FXML
    private Label movieLabel, hallLabel, startTimeLabel, endTimeLabel;

    @FXML
    private void searchSessionById() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Поле ID не может быть пустым.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int sessionId = Integer.parseInt(idText);
            String message = "SESSION;GET;" + sessionId;

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(message);

                String response = in.readLine();
                if (response == null) {
                    AppUtils.showAlert("Ошибка", "Ответ сервера отсутствует.", Alert.AlertType.ERROR);
                    return;
                }
                if (response.startsWith("SESSION_FOUND;")) {
                    String[] sessionData = response.split(";");
                    movieLabel.setText(sessionData[2]);
                    hallLabel.setText(sessionData[3]);
                    startTimeLabel.setText(formatDateTime(sessionData[4]));
                    endTimeLabel.setText(formatDateTime(sessionData[5]));
                } else if (response.equals("SESSION_NOT_FOUND")) {
                    AppUtils.showAlert("Ошибка", "Сеанс с указанным ID не найден.", Alert.AlertType.WARNING);
                } else if (response.startsWith("ERROR;")) {
                    AppUtils.showAlert("Ошибка", response.split(";", 2)[1], Alert.AlertType.ERROR);
                }
            }
        } catch (NumberFormatException e) {
            AppUtils.showAlert("Ошибка", "ID должен быть числом.", Alert.AlertType.WARNING);
        } catch (IOException e) {
            AppUtils.showAlert("Ошибка", "Не удалось подключиться к серверу.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    private String formatDateTime(String dateTime) {
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
            return parsedDateTime.format(formatter);
        } catch (Exception e) {
            return dateTime;
        }
    }
}
