package Admin.TicketActions;

import Utils.AppUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class FindTicket {
    @FXML
    private TextField idField;
    @FXML
    private Label sessionIdLabel, seatNumberLabel, userIdLabel, priceLabel, purchaseTimeLabel;
    @FXML
    public Button backButton;

    @FXML
    private void searchTicketById() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Поле ID не может быть пустым.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int ticketId = Integer.parseInt(idText);
            String message = "TICKET;GET;" + ticketId;

            try (Socket socket = new Socket("localhost", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(message);

                String response = in.readLine();
                if (response == null) {
                    AppUtils.showAlert("Ошибка", "Ответ сервера отсутствует.", Alert.AlertType.ERROR);
                    return;
                }
                if (response.startsWith("TICKET_FOUND;")) {
                    String[] ticketData = response.split(";");
                    sessionIdLabel.setText(ticketData[2]);
                    seatNumberLabel.setText(ticketData[3] + " место");
                    userIdLabel.setText(ticketData[4]);
                    priceLabel.setText(ticketData[5] + " рублей");
                    purchaseTimeLabel.setText(formatDateTime(ticketData[6]));
                    System.out.println(response);
                } else if (response.equals("TICKET_NOT_FOUND")) {
                    AppUtils.showAlert("Ошибка", "Билет с указанным ID не найден.", Alert.AlertType.WARNING);
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

    private String formatDateTime(String dateTime) {
        try {
            LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
            return parsedDateTime.format(formatter);
        } catch (Exception e) {
            return dateTime;
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}
