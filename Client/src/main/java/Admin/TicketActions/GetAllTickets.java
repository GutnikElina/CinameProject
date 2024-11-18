package Admin.TicketActions;

import Utils.AppUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GetAllTickets {

    @FXML
    private ListView<String> ticketsListView;
    @FXML
    private Button backButton;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
    private String command = "TICKET;GET_ALL;";

    @FXML
    private void initialize() {
        fetchAllTickets();
        backButton.setOnAction(e -> handleBackButton());
    }

    @FXML
    private void fetchAllTickets() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);

            String response;
            List<String> tickets = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_TICKETS")) {
                    break;
                }
                if (response.startsWith("TICKET_FOUND;")) {
                    String[] ticketData = response.split(";");
                    String ticketId = ticketData[1];
                    String sessionId = ticketData[2];
                    String userId = ticketData[4];
                    String seatNumber = ticketData[3];
                    String price = ticketData[5];
                    String status = ticketData[6];
                    String requestType = ticketData[7];
                    String purchaseTime = formatDateTime(ticketData[8]);

                    tickets.add(String.format(
                            "ID билета: %s\nID сеанса: %s\nID пользователя: %s\nМесто: %s\nЦена: %s\nСтатус: %s\nТип запроса: %s\nВремя покупки: %s",
                            ticketId, sessionId, userId, seatNumber, price, status, requestType, purchaseTime
                    ));
                } else if (response.startsWith("ERROR;")) {
                    AppUtils.showAlert("Ошибка", "Не удалось получить данные о билетах.", Alert.AlertType.ERROR);
                    break;
                }
            }
            updateTicketsListView(tickets);

        } catch (IOException e) {
            AppUtils.showAlert("Ошибка", "Не удалось подключиться к серверу.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String formatDateTime(String dateTime) {
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
            return parsedDateTime.format(dateFormatter);
        } catch (Exception e) {
            return dateTime;
        }
    }

    private void updateTicketsListView(List<String> tickets) {
        ticketsListView.getItems().clear();
        if (tickets.isEmpty()) {
            ticketsListView.getItems().add("Нет билетов.");
        } else {
            ticketsListView.getItems().addAll(tickets);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}