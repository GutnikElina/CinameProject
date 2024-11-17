package Admin.TicketActions;

import Utils.UIUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import Utils.AppUtils;
import java.math.BigDecimal;

public class UpdateTicket extends Application {

    private TextField ticketIdField, seatNumberField, priceField;
    private ComboBox<String> sessionComboBox;
    private Label ticketIdLabel, seatLabel, priceLabel, sessionLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Обновить билет");

        ticketIdField = new TextField();
        ticketIdField.setPromptText("ID билета");

        sessionComboBox = new ComboBox<>();
        loadSessionData();

        seatNumberField = new TextField();
        seatNumberField.setPromptText("Номер места");

        priceField = new TextField();
        priceField.setPromptText("Цена");

        ticketIdLabel = new Label("ID билета:");
        sessionLabel = new Label("Выберите сеанс:");
        seatLabel = new Label("Номер места:");
        priceLabel = new Label("Цена билета:");

        Button updateButton = new Button("Обновить билет");
        updateButton.setOnAction(e -> updateTicketAction(primaryStage));

        VBox vbox = new VBox(15, ticketIdLabel, ticketIdField, sessionLabel, sessionComboBox,
                seatLabel, seatNumberField, priceLabel, priceField, updateButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadSessionData() {
        String sessionCommand = "SESSION;GET_ALL";
        String sessionResponse = AppUtils.sendToServerAndGetFullResponse(sessionCommand);
        if (sessionResponse.startsWith("SESSION_FOUND")) {
            String[] sessionLines = sessionResponse.split("\n");
            for (String line : sessionLines) {
                if (line.startsWith("SESSION_FOUND")) {
                    String[] sessionParts = line.split(";");
                    String sessionDetails = sessionParts[1];
                    sessionComboBox.getItems().add(sessionDetails);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить сеансы: " + sessionResponse, Alert.AlertType.ERROR);
        }
    }

    private void updateTicketAction(Stage stage) {
        try {
            String ticketIdStr = ticketIdField.getText();
            String sessionDetails = sessionComboBox.getValue();
            String seatNumber = seatNumberField.getText();
            String priceStr = priceField.getText();

            if (ticketIdStr.isEmpty() || sessionDetails == null || seatNumber.isEmpty() || priceStr.isEmpty()) {
                throw new IllegalArgumentException("Все поля должны быть заполнены!");
            }

            int ticketId = Integer.parseInt(ticketIdStr);
            int sessionId = Integer.parseInt(sessionDetails.split(";")[0]);
            BigDecimal price = new BigDecimal(priceStr).setScale(2, BigDecimal.ROUND_HALF_UP);

            String updateTicketCommand = String.format("TICKET;UPDATE;%d;%d;%s;%s", ticketId, sessionId, seatNumber, price);

            String response = AppUtils.sendToServer(updateTicketCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Билет обновлен", Alert.AlertType.INFORMATION);
                stage.close();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось обновить билет: " + response, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось обновить билет: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
