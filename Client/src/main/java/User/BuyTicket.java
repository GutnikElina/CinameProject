package User;

import Elements.Menu.UserMenu;
import Utils.AppUtils;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.Arrays;

public class BuyTicket {

    @FXML private ComboBox<String> sessionComboBox;
    @FXML private TextField seatNumberField;
    @FXML private Button checkAvailabilityButton;
    @FXML private Button buyButton;
    @FXML private Button backButton;

    @FXML
    private void initialize() {
        loadSessionData();
        checkAvailabilityButton.setOnAction(e -> checkTicketAvailability());
        buyButton.setOnAction(e -> buyTicketAction());
        backButton.setOnAction(e -> handleBackButton());
    }

    private void loadSessionData() {
        String sessionResponse = AppUtils.sendToServerAndGetFullResponse("SESSION;GET_ALL");
        if (sessionResponse.startsWith("SESSION_FOUND")) {
            Arrays.stream(sessionResponse.split("\n"))
                    .filter(line -> line.startsWith("SESSION_FOUND"))
                    .map(line -> line.split(";"))
                    .filter(parts -> parts.length >= 2)
                    .forEach(parts -> sessionComboBox.getItems()
                            .add(String.format("ID: %s | Фильм: %s", parts[1], parts[2])));
        } else {
            showErrorAlert("Не удалось загрузить сеансы", sessionResponse);
        }
    }

    private void checkTicketAvailability() {
        String sessionDetails = sessionComboBox.getValue();
        String seatNumber = seatNumberField.getText();
        if (isInputValid(sessionDetails, seatNumber)) {
            int sessionId = extractSessionId(sessionDetails);
            if (sessionId == -1) return;

            int seatNum = parseSeatNumber(seatNumber);
            if (seatNum == -1) return;

            int hallCapacity = getHallCapacity(sessionId);
            if (seatNum > hallCapacity) {
                showCapacityAlert(hallCapacity);
                return;
            }
            checkTicketAvailabilityWithServer(sessionId, seatNumber);
        }
    }

    private boolean isInputValid(String sessionDetails, String seatNumber) {
        if (sessionDetails == null || seatNumber.isEmpty()) {
            showErrorAlert("Выберите сеанс и введите номер места", "");
            return false;
        }
        return true;
    }

    private void checkTicketAvailabilityWithServer(int sessionId, String seatNumber) {
        String checkTicketCommand = String.format("TICKET;CHECK;%d;%s", sessionId, seatNumber);
        String checkResponse = AppUtils.sendJsonCommandToServer(checkTicketCommand);
        if (checkResponse.startsWith("TICKET_NOT_EXISTS")) {
            UIUtils.showAlert("Доступно", "Место доступно для покупки.", Alert.AlertType.INFORMATION);
        } else if (checkResponse.startsWith("TICKET_EXISTS")) {
            UIUtils.showAlert("Занято", "Место уже занято.", Alert.AlertType.WARNING);
        } else {
            showErrorAlert("Не удалось проверить доступность места", checkResponse);
        }
    }

    private void buyTicketAction() {
        String sessionDetails = sessionComboBox.getValue();
        String seatNumber = seatNumberField.getText();

        if (isInputValid(sessionDetails, seatNumber)) {
            int sessionId = extractSessionId(sessionDetails);
            if (sessionId == -1) return;

            if (checkIfTicketOccupied(sessionId, seatNumber)) return;

            BigDecimal price = getSessionPrice(sessionId);
            if (price == null) return;

            int userId = AppUtils.getCurrentUserId(UserMenu.userToken);
            if (userId == -1) {
                showErrorAlert("Не удалось определить пользователя", "");
                return;
            }

            String buyTicketCommand = String.format("TICKET;ADD;%d;%s;%d;%s;PENDING;PURCHASE", sessionId, seatNumber, userId, price);
            String response = AppUtils.sendJsonCommandToServer(buyTicketCommand);
            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Билет успешно куплен.", Alert.AlertType.INFORMATION);
            } else {
                showErrorAlert("Не удалось купить билет", response);
            }
        }
    }

    private boolean checkIfTicketOccupied(int sessionId, String seatNumber) {
        String checkTicketCommand = String.format("TICKET;CHECK;%d;%s", sessionId, seatNumber);
        String checkResponse = AppUtils.sendJsonCommandToServer(checkTicketCommand);
        if (checkResponse.startsWith("TICKET_EXISTS")) {
            UIUtils.showAlert("Занято", "Место уже занято.", Alert.AlertType.WARNING);
            return true;
        }
        return false;
    }

    private int extractSessionId(String sessionDetails) {
        try {
            return Integer.parseInt(sessionDetails.split("\\|")[0].replace("ID:", "").trim());
        } catch (NumberFormatException e) {
            showErrorAlert("Некорректный ID сеанса", "");
            return -1;
        }
    }

    private int parseSeatNumber(String seatNumber) {
        try {
            return Integer.parseInt(seatNumber);
        } catch (NumberFormatException e) {
            showErrorAlert("Некорректный номер места", "");
            return -1;
        }
    }

    private void showErrorAlert(String message, String detailedMessage) {
        UIUtils.showAlert("Ошибка", message + (detailedMessage.isEmpty() ? "" : ": " + detailedMessage), Alert.AlertType.ERROR);
    }

    private BigDecimal getSessionPrice(int sessionId) {
        String sessionResponse = AppUtils.sendJsonCommandToServer(String.format("SESSION;GET;%d", sessionId));
        if (!sessionResponse.startsWith("SESSION_FOUND")) {
            showErrorAlert("Сеанс не найден", sessionResponse);
            return null;
        }

        String[] sessionParts = sessionResponse.split(";");
        try {
            return new BigDecimal(sessionParts[6]);
        } catch (Exception e) {
            showErrorAlert("Не удалось получить цену сеанса", "");
            return null;
        }
    }

    private int getHallCapacity(int sessionId) {
        String sessionResponse = AppUtils.sendJsonCommandToServer(String.format("SESSION;GET;%d", sessionId));
        if (!sessionResponse.startsWith("SESSION_FOUND")) {
            showErrorAlert("Сеанс не найден", sessionResponse);
            return 0;
        }

        String[] sessionParts = sessionResponse.split(";");
        String hallName = sessionParts[3];
        String hallResponse = AppUtils.sendJsonCommandToServer(String.format("HALL;GET;%s", hallName));

        if (hallResponse.startsWith("HALL_FOUND")) {
            String[] hallParts = hallResponse.split(";");
            try {
                return Integer.parseInt(hallParts[3]);
            } catch (NumberFormatException e) {
                showErrorAlert("Некорректная вместимость зала", "");
            }
        } else {
            showErrorAlert("Не удалось получить данные о зале", hallResponse);
        }
        return 0;
    }

    private void showCapacityAlert(int hallCapacity) {
        UIUtils.showAlert("Ошибка", "Место превышает вместимость зала!", Alert.AlertType.ERROR);
        UIUtils.showAlert("Информация", "Вместимость зала: " + hallCapacity, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}