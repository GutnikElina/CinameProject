package Admin.SessionActions;

import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class FindSession {
    @FXML public Button backButton;
    @FXML public Label priceLabel;
    @FXML private TextField idField;
    @FXML private Label movieLabel, hallLabel, startTimeLabel, endTimeLabel;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void searchSessionById() {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            UIUtils.showAlert("Ошибка", "Поле ID не может быть пустым.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int sessionId = Integer.parseInt(idText);

            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand("SESSION;GET");
            requestDTO.setData(Map.of("sessionId", String.valueOf(sessionId)));

            String jsonRequest = gson.toJson(requestDTO);
            String jsonResponse = AppUtils.sendJsonCommandToServer(jsonRequest);
            ResponseDTO response = gson.fromJson(jsonResponse, ResponseDTO.class);

            if ("SESSION_FOUND".equals(response.getStatus())) {
                populateSessionData(response.getData());
            } else if ("SESSION_NOT_FOUND".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", "Сеанс с указанным ID не найден.", Alert.AlertType.WARNING);
            } else {
                UIUtils.showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "ID должен быть числом.", Alert.AlertType.WARNING);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Произошла ошибка при обработке данных: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void populateSessionData(Map<String, Object> sessionData) {
        try {
            String movieTitle = (String) sessionData.get("movieTitle");
            String hallName = (String) sessionData.get("hallName");
            String startTime = (String) sessionData.get("startTime");
            String endTime = (String) sessionData.get("endTime");
            String priceValue = sessionData.get("price").toString();

            DateTimeFormatter serverFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.forLanguageTag("ru"));

            LocalDateTime startDateTime = LocalDateTime.parse(startTime, serverFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endTime, serverFormatter);

            movieLabel.setText(movieTitle);
            hallLabel.setText(hallName);
            startTimeLabel.setText(startDateTime.format(displayFormatter));
            endTimeLabel.setText(endDateTime.format(displayFormatter));
            priceLabel.setText(new BigDecimal(priceValue) + " рублей");

        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при обработке данных сеанса: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    @FXML
    private void handleBack() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}