package Admin.TicketActions;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddTicket {
    @FXML
    private ComboBox<String> sessionComboBox;
    @FXML
    private ComboBox<String> userComboBox;
    @FXML
    private TextField seatNumberField;
    @FXML
    private TextField priceField;
    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private ComboBox<String> requestTypeComboBox;

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
        String sessionCommand = "SESSION;GET_ALL";
        String sessionResponse = AppUtils.sendToServerAndGetFullResponse(sessionCommand);
        if (sessionResponse.startsWith("SESSION_FOUND")) {
            String[] sessionLines = sessionResponse.split("\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
            for (String line : sessionLines) {
                if (line.startsWith("SESSION_FOUND")) {
                    String[] sessionParts = line.split(";");
                    if (sessionParts.length >= 6) {
                        try {
                            LocalDateTime startTime = LocalDateTime.parse(sessionParts[4]);
                            LocalDateTime endTime = LocalDateTime.parse(sessionParts[5]);
                            String sessionDetails = String.format(
                                    "ID: %s | Фильм: %s | Зал: %s | Начало: %s | Конец: %s",
                                    sessionParts[1], sessionParts[2], sessionParts[3],
                                    startTime.format(formatter), endTime.format(formatter)
                            );
                            sessionComboBox.getItems().add(sessionDetails);
                        } catch (Exception e) {
                            UIUtils.showAlert("Ошибка", "Ошибка форматирования даты/времени: " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
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
                    if (userParts.length >= 4) {
                        String userDetails = userParts[1] + " - " + userParts[2] + " (" + userParts[3] + ")";
                        userComboBox.getItems().add(userDetails);
                    } else {
                        UIUtils.showAlert("Ошибка", "Некорректный формат данных пользователя: " + line, Alert.AlertType.ERROR);
                    }
                }
            }
        } else if (userResponse.startsWith("USER_NOT_FOUND")) {
            UIUtils.showAlert("Информация", "Пользователи не найдены.", Alert.AlertType.INFORMATION);
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить пользователей: " + userResponse, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void addTicketAction() {
        try {
            String sessionDetails = sessionComboBox.getValue();
            String userDetails = userComboBox.getValue();
            String seatNumber = seatNumberField.getText();
            String priceStr = priceField.getText();
            String status = statusComboBox.getValue();
            String requestType = requestTypeComboBox.getValue();

            if (sessionDetails == null || userDetails == null || seatNumber.isEmpty() || priceStr.isEmpty()
                    || status == null || requestType == null) {
                throw new IllegalArgumentException("Все поля должны быть заполнены!");
            }

            int sessionId = Integer.parseInt(sessionDetails.split("\\|")[0].replace("ID:", "").trim());
            int userId = Integer.parseInt(userDetails.split(" - ")[0]);
            BigDecimal price = new BigDecimal(priceStr).setScale(2, BigDecimal.ROUND_HALF_UP);

            String checkTicketCommand = String.format("TICKET;CHECK;%d;%s", sessionId, seatNumber);
            String checkResponse = AppUtils.sendToServer(checkTicketCommand);

            if (checkResponse.startsWith("TICKET_EXISTS")) {
                UIUtils.showAlert("Внимание", "Билет уже куплен на это место для данного сеанса.", Alert.AlertType.WARNING);
                return;
            }

            String addTicketCommand = String.format("TICKET;ADD;%d;%s;%d;%s;%s;%s",
                    sessionId, seatNumber, userId, price, status, requestType);
            String response = AppUtils.sendToServer(addTicketCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Билет добавлен", Alert.AlertType.INFORMATION);
                handleBackButton();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось добавить билет: " + response, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось добавить билет: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
