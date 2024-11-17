package Admin.TicketActions;

import Utils.AppUtils;
import javafx.application.Application;
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

public class FindTicket extends Application {

    private TextField idField;
    private Label sessionIdLabel, seatNumberLabel, userIdLabel, priceLabel, purchaseTimeLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Поиск билета по ID");

        idField = new TextField();
        idField.setPromptText("Введите ID билета");
        idField.setPrefWidth(300);

        Button searchButton = new Button("Найти");
        searchButton.setOnAction(e -> searchTicketById());

        HBox inputPanel = new HBox(10, new Label("ID билета:"), idField, searchButton);
        inputPanel.setAlignment(Pos.CENTER);
        inputPanel.setPadding(new Insets(10));

        sessionIdLabel = createInfoLabel("");
        seatNumberLabel = createInfoLabel("");
        userIdLabel = createInfoLabel("");
        priceLabel = createInfoLabel("");
        purchaseTimeLabel = createInfoLabel("");

        GridPane ticketInfoPane = new GridPane();
        ticketInfoPane.setHgap(10);
        ticketInfoPane.setVgap(10);
        ticketInfoPane.setPadding(new Insets(20));
        ticketInfoPane.addRow(0, new Label("ID Сеанса:"), sessionIdLabel);
        ticketInfoPane.addRow(1, new Label("Место:"), seatNumberLabel);
        ticketInfoPane.addRow(2, new Label("ID Пользователя:"), userIdLabel);
        ticketInfoPane.addRow(3, new Label("Цена:"), priceLabel);
        ticketInfoPane.addRow(4, new Label("Время покупки:"), purchaseTimeLabel);
        ticketInfoPane.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 8px; -fx-padding: 10;");

        Button backButton = new Button("Назад");
        backButton.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(20, inputPanel, ticketInfoPane, backButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        return label;
    }

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
                    sessionIdLabel.setText(ticketData[1]);
                    seatNumberLabel.setText(ticketData[2]);
                    userIdLabel.setText(ticketData[3]);
                    priceLabel.setText(ticketData[4]);
                    purchaseTimeLabel.setText(formatDateTime(ticketData[5]));
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

    public static void main(String[] args) {
        launch(args);
    }
}