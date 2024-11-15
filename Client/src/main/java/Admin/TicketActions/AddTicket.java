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

public class AddTicket extends Application {

    private ComboBox<String> sessionComboBox;
    private TextField seatNumberField, priceField;
    private Label seatLabel, priceLabel, sessionLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Добавить билет");

        sessionComboBox = new ComboBox<>();
        loadSessionData();

        seatNumberField = new TextField();
        seatNumberField.setPromptText("Номер места");

        priceField = new TextField();
        priceField.setPromptText("Цена");

        seatLabel = new Label("Номер места:");
        priceLabel = new Label("Цена билета:");
        sessionLabel = new Label("Выберите сеанс:");

        Button addButton = new Button("Добавить билет");
        addButton.setOnAction(e -> addTicketAction(primaryStage));

        VBox vbox = new VBox(15, sessionLabel, sessionComboBox,
                seatLabel, seatNumberField,
                priceLabel, priceField,
                addButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9;");

        Scene scene = new Scene(vbox, 400, 300);
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
                    String sessionDetails = sessionParts[2];
                    sessionComboBox.getItems().add(sessionDetails);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить сеансы: " + sessionResponse, Alert.AlertType.ERROR);
        }
    }

    private void addTicketAction(Stage stage) {
        try {
            String sessionDetails = sessionComboBox.getValue();
            String seatNumber = seatNumberField.getText();
            String priceStr = priceField.getText();

            if (sessionDetails == null || seatNumber == null || priceStr == null) {
                throw new IllegalArgumentException("Все поля должны быть заполнены!");
            }

            int sessionId = Integer.parseInt(sessionDetails.split(";")[0]);
            BigDecimal price = new BigDecimal(priceStr);
            String userId = "1";

            String addTicketCommand = String.format("TICKET;ADD;%d;%s;%d;%s", sessionId, seatNumber, Integer.parseInt(userId), price);
            String response = AppUtils.sendToServer(addTicketCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Билет добавлен", Alert.AlertType.INFORMATION);
                stage.close();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось добавить билет: " + response, Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось добавить билет: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
