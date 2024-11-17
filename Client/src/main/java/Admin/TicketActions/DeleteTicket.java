package Admin.TicketActions;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeleteTicket extends Application {
    private TextField ticketIdField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Удалить билет");

        ticketIdField = new TextField();
        ticketIdField.setPromptText("Введите ID билета");

        Button deleteButton = new Button("Удалить билет");
        deleteButton.setOnAction(e -> deleteTicketAction(primaryStage));

        VBox vbox = new VBox(10, ticketIdField, deleteButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deleteTicketAction(Stage stage) {
        try {
            String ticketIdStr = ticketIdField.getText();

            if (ticketIdStr == null || ticketIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("ID билета не должен быть пустым");
            }

            int ticketId = Integer.parseInt(ticketIdStr);

            String deleteTicketCommand = String.format("TICKET;DELETE;%d", ticketId);
            String response = AppUtils.sendToServer(deleteTicketCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Билет удален", Alert.AlertType.INFORMATION);
                stage.close();
            } else if (response.equals("TICKET_NOT_FOUND")) {
                UIUtils.showAlert("Ошибка", "Билет с таким ID не найден", Alert.AlertType.ERROR);
                stage.close();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось удалить билет: " + response, Alert.AlertType.ERROR);
                stage.close();
            }

        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "ID билета должен быть числом", Alert.AlertType.ERROR);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось удалить билет: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
