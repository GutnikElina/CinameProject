package Admin.TicketActions;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeleteTicket {

    @FXML
    private TextField ticketIdField;
    @FXML
    private Button backButton;

    @FXML
    private void deleteTicketAction() {
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
                handleBackButton();
            } else if (response.equals("TICKET_NOT_FOUND")) {
                UIUtils.showAlert("Ошибка", "Билет с таким ID не найден", Alert.AlertType.ERROR);
                handleBackButton();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось удалить билет: " + response, Alert.AlertType.ERROR);
                handleBackButton();
            }

        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "ID билета должен быть числом", Alert.AlertType.ERROR);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось удалить билет: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) ticketIdField.getScene().getWindow();
        stage.close();
    }
}
