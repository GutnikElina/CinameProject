package Admin.TicketActions;

import Utils.AppUtils;
import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class GetAllTickets {

    @FXML private ListView<String> ticketsListView;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

    @FXML
    private void initialize() {
        fetchAllTickets();
        backButton.setOnAction(e -> handleBackButton());
    }

    @FXML
    private void fetchAllTickets() {
        try {
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand("TICKET;GET_ALL");
            String requestJson = gson.toJson(requestDTO);

            String responseJson = AppUtils.sendJsonCommandToServer(requestJson);

            ResponseDTO response = gson.fromJson(responseJson, ResponseDTO.class);
            if ("SUCCESS".equals(response.getStatus())) {
                List<Map<String, Object>> tickets = (List<Map<String, Object>>) response.getData().get("tickets");
                updateTicketsListView(tickets);
            } else {
                UIUtils.showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось подключиться к серверу: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateTicketsListView(List<Map<String, Object>> tickets) {
        ticketsListView.getItems().clear();
        if (tickets == null || tickets.isEmpty()) {
            ticketsListView.getItems().add("Нет билетов.");
        } else {
            for (Map<String, Object> ticket : tickets) {
                String id = String.valueOf(ticket.get("id"));
                String sessionId = String.valueOf(ticket.get("sessionId"));
                String userId = String.valueOf(ticket.get("userId"));
                String seatNumber = String.valueOf(ticket.get("seatNumber"));
                String price = String.valueOf(ticket.get("price"));
                String status = String.valueOf(ticket.get("status"));
                String requestType = String.valueOf(ticket.get("requestType"));
                String purchaseTimeRaw = String.valueOf(ticket.get("purchaseTime"));
                String formattedDateTime = formatDateTime(purchaseTimeRaw);

                String ticketDetails = String.format(
                        "ID билета: %s\nID сеанса: %s\nID пользователя: %s\nМесто: %s\nЦена: %s\nСтатус: %s\nТип запроса: %s\nВремя покупки: %s",
                        id, sessionId, userId, seatNumber, price, status, requestType, formattedDateTime
                );
                ticketsListView.getItems().add(ticketDetails);
            }
        }
    }

    private String formatDateTime(String dateTime) {
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
            return parsedDateTime.format(formatter);
        } catch (Exception e) {
            return dateTime;
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
