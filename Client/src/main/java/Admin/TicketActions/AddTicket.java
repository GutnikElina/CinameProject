package Admin.TicketActions;

import Models.RequestDTO;
import Models.ResponseDTO;
import Models.Ticket;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AddTicket {
    @FXML private ComboBox<String> sessionComboBox;
    @FXML private ComboBox<String> userComboBox;
    @FXML private TextField seatNumberField;
    @FXML private Button backButton;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> requestTypeComboBox;
    private final Gson gson = GsonFactory.create();

    @FXML
    public void initialize() {
        loadUserData();
        loadSessionData();
        loadStatusOptions();
        loadRequestTypeOptions();
    }

    private void loadStatusOptions() {
        statusComboBox.getItems().addAll("PENDING", "CONFIRMED", "CANCELLED", "EXCHANGED");
    }

    private void loadRequestTypeOptions() {
        requestTypeComboBox.getItems().addAll("PURCHASE", "CANCEL", "EXCHANGE");
    }

    private void loadSessionData() {
        try {
            RequestDTO requestDTO = new RequestDTO("SESSION;GET_ALL", Map.of());
            String jsonResponse = AppUtils.sendJsonCommandToServer(gson.toJson(requestDTO));

            ResponseDTO response = gson.fromJson(jsonResponse, ResponseDTO.class);

            if ("SUCCESS".equals(response.getStatus()) && response.getData() != null) {
                List<Map<String, Object>> sessions = (List<Map<String, Object>>) response.getData().get("data");
                sessionComboBox.getItems().addAll(
                        sessions.stream()
                                .map(session -> String.format("ID:%d | %s",
                                        ((Number) session.get("id")).intValue(),
                                        session.get("movieTitle")))
                                .toList()
                );
            } else {
                handleError(response.getMessage());
            }
        } catch (Exception e) {
            handleError("Не удалось загрузить сеансы: " + e.getMessage());
        }
    }

    private void loadUserData() {
        try {
            RequestDTO requestDTO = new RequestDTO("USER;GET_ALL", Map.of());
            String jsonResponse = AppUtils.sendJsonCommandToServer(gson.toJson(requestDTO));

            ResponseDTO response = gson.fromJson(jsonResponse, ResponseDTO.class);

            if ("SUCCESS".equals(response.getStatus()) && response.getData() != null) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getData().get("data");
                userComboBox.getItems().addAll(
                        data.stream()
                                .map(user -> String.format("%d - %s",
                                        ((Number) user.get("id")).intValue(),
                                        user.get("username")))
                                .toList()
                );
            } else {
                handleError(response.getMessage());
            }
        } catch (Exception e) {
            handleError("Не удалось загрузить пользователей: " + e.getMessage());
        }
    }

    private void handleError(String message) {
        UIUtils.showAlert("Ошибка", message, Alert.AlertType.ERROR);
    }

    @FXML
    private void addTicketAction() {
        try {
            validateFields();

            Ticket ticket = new Ticket(
                    null,
                    extractSessionId(sessionComboBox.getValue()),
                    extractUserId(userComboBox.getValue()),
                    seatNumberField.getText(),
                    statusComboBox.getValue(),
                    requestTypeComboBox.getValue(),
                    LocalDateTime.now()
            );

            RequestDTO requestDTO = new RequestDTO("TICKET;ADD", Map.of("data", gson.toJson(ticket)));
            String jsonResponse = AppUtils.sendJsonCommandToServer(gson.toJson(requestDTO));
            ResponseDTO response = gson.fromJson(jsonResponse, ResponseDTO.class);

            if ("SUCCESS".equals(response.getStatus())) {
                UIUtils.showAlert("Успешное выполнение", response.getMessage(), Alert.AlertType.INFORMATION);
                handleBackButton();
            } else {
                handleError(response.getMessage());
            }
        } catch (Exception e) {
            handleError("Не удалось добавить билет: " + e.getMessage());
        }
    }

    private void validateFields() {
        if (sessionComboBox.getValue() == null || userComboBox.getValue() == null
                || seatNumberField.getText().isEmpty() || statusComboBox.getValue() == null
                || requestTypeComboBox.getValue() == null) {
            throw new IllegalArgumentException("Все поля должны быть заполнены!");
        }
    }

    private int extractSessionId(String sessionDetails) {
        return Integer.parseInt(sessionDetails.split("\\|")[0].replace("ID:", "").trim());
    }

    private int extractUserId(String userDetails) {
        return Integer.parseInt(userDetails.split(" - ")[0]);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}