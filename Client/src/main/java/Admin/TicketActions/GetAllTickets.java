package Admin.TicketActions;

import Utils.AppUtils;
import javafx.application.Application;
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

public class GetAllTickets extends Application {

    private ListView<String> ticketsListView;
    private Button backButton;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Все билеты");

        ticketsListView = new ListView<>();
        ticketsListView.setPrefHeight(300);

        backButton = new Button("Назад");
        backButton.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(ticketsListView, backButton);

        fetchAllTickets();

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchAllTickets() {
        String message = "TICKET;GET_ALL;";

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);

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
                    String seatNumber = ticketData[3];
                    String userId = ticketData[4];
                    String price = ticketData[5];
                    String purchaseTime = formatDateTime(ticketData[6]);

                    tickets.add(String.format("ID билета: %s\nID сеанса: %s\nМесто: %s\nID пользователя: %s\nЦена: %s\nВремя покупки: %s",
                            ticketId, sessionId, seatNumber, userId, price, purchaseTime));
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

    public static void main(String[] args) {
        launch(args);
    }
}
