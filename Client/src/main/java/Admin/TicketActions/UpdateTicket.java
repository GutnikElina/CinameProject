package Admin.TicketActions;

import Admin.GeneralActions.TicketActionBase;
import Models.ResponseDTO;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Utils.AppUtils;
import Models.Ticket;
import Models.RequestDTO;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UpdateTicket extends TicketActionBase {

    @FXML public Button updateButton;
    @FXML private ComboBox<String> sessionComboBox;
    @FXML private ComboBox<String> userComboBox;
    @FXML private TextField seatNumberField;
    @FXML private TextField ticketIdField;
    @FXML private Button backButton;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> requestTypeComboBox;
    private final Gson gson = new Gson();

    @FXML
    private void initialize() {
        loadSessionData();
        loadUserData();
        loadStatusData();
        loadRequestTypeData();
    }

    private void loadStatusData() {
        statusComboBox.getItems().addAll("PENDING", "CONFIRMED", "CANCELLED", "EXCHANGED");
    }

    private void loadRequestTypeData() {
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
    private void updateTicketAction() {
        boolean valid = true;
        valid &= FieldValidator.validateTextField(ticketIdField, "Введите корректный ID билета.", 1);
        valid &= FieldValidator.validateComboBox(sessionComboBox, "Выберите сеанс.");
        valid &= FieldValidator.validateTextField(seatNumberField, "Укажите номер места.", 1);
        valid &= FieldValidator.validateComboBox(statusComboBox, "Выберите статус.");
        valid &= FieldValidator.validateComboBox(requestTypeComboBox, "Выберите тип запроса.");
        valid &= FieldValidator.validateComboBox(userComboBox, "Выберите пользователя.");

        if (!valid) return;

        try {
            String ticketIdStr = ticketIdField.getText().trim();
            String sessionDetails = sessionComboBox.getValue();
            String seatNumber = seatNumberField.getText().trim();
            String status = statusComboBox.getValue();
            String requestType = requestTypeComboBox.getValue();
            String userStr = userComboBox.getValue();

            int ticketId = Integer.parseInt(ticketIdStr);
            int sessionId = extractSessionId(sessionDetails);

            int userId = Integer.parseInt(userStr.split(" - ")[0]);

            Ticket ticket = new Ticket();
            ticket.setId(ticketId);
            ticket.setSessionId(sessionId);
            ticket.setSeatNumber(seatNumber);
            ticket.setStatus(status);
            ticket.setRequestType(requestType);
            ticket.setUserId(userId);
            ticket.setPurchaseTime(LocalDateTime.now());

            Stage stage = (Stage) updateButton.getScene().getWindow();
            sendTicketCommand(ticket, stage);

        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось обновить билет: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int extractSessionId(String sessionDetails) {
        try {
            String sessionIdStr = sessionDetails.split(" \\| ")[0];
            return Integer.parseInt(sessionIdStr.replace("ID:", "").trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось извлечь ID сеанса: " + sessionDetails);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
