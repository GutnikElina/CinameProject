package Admin.TicketActions;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Utils.AppUtils;
import java.math.BigDecimal;

public class UpdateTicket {

    @FXML
    private ComboBox<String> sessionComboBox;
    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private TextField seatNumberField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField ticketIdField;
    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private ComboBox<String> requestTypeComboBox;

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
        String sessionCommand = "SESSION;GET_ALL";
        String sessionResponse = AppUtils.sendToServerAndGetFullResponse(sessionCommand);
        if (sessionResponse.startsWith("SESSION_FOUND")) {
            String[] sessionLines = sessionResponse.split("\n");
            for (String line : sessionLines) {
                if (line.startsWith("SESSION_FOUND")) {
                    String[] sessionParts = line.split(";");
                    String sessionDetails = sessionParts[1]; // Пример: сеанс в формате "ID;Фильм;Зал;Время"
                    sessionComboBox.getItems().add(sessionDetails);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить сеансы: " + sessionResponse, Alert.AlertType.ERROR);
        }
    }

    private void loadUserData() {
        String userCommand = "USER;GET_ALL";
        String userResponse = AppUtils.sendToServerAndGetFullResponse(userCommand);
        if (userResponse.startsWith("USER_FOUND")) {
            String[] userLines = userResponse.split("\n");
            for (String line : userLines) {
                if (line.startsWith("USER_FOUND")) {
                    String[] userParts = line.split(";");
                    String userDetails = userParts[1] + " (" + userParts[2] + ")";
                    userComboBox.getItems().add(userDetails);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить пользователей: " + userResponse, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void updateTicketAction() {
        try {
            String ticketIdStr = ticketIdField.getText();
            String sessionDetails = sessionComboBox.getValue();
            String seatNumber = seatNumberField.getText();
            String priceStr = priceField.getText();
            String status = statusComboBox.getValue();
            String requestType = requestTypeComboBox.getValue();

            if (ticketIdStr.isEmpty() || sessionDetails == null || seatNumber.isEmpty() || priceStr.isEmpty()
                    || status == null || requestType == null) {
                throw new IllegalArgumentException("Все поля должны быть заполнены!");
            }

            int ticketId = Integer.parseInt(ticketIdStr);
            int sessionId = Integer.parseInt(sessionDetails.split(";")[0]);
            BigDecimal price = new BigDecimal(priceStr).setScale(2, BigDecimal.ROUND_HALF_UP);

            String updateTicketCommand = String.format("TICKET;UPDATE;ALL;%d;%d;%s;%s;%s;%s",
                    ticketId, sessionId, seatNumber, price, status, requestType);

            String response = AppUtils.sendToServer(updateTicketCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Билет обновлен", Alert.AlertType.INFORMATION);
                handleBackButton();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось обновить билет: " + response, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось обновить билет: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
