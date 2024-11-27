package Admin.TicketActions;

import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import Models.RequestDTO;
import Models.ResponseDTO;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Map;

public class DeleteTicket {

    @FXML private TextField ticketIdField;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void deleteTicketAction() {
        try {
            String ticketIdStr = ticketIdField.getText();

            if (ticketIdStr == null || ticketIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("ID билета не должен быть пустым");
            }

            int ticketId = Integer.parseInt(ticketIdStr);
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand("TICKET;DELETE");
            requestDTO.setData(Map.of("ticketId", String.valueOf(ticketId)));
            String requestJson = gson.toJson(requestDTO);
            String responseJson = AppUtils.sendJsonCommandToServer(requestJson);
            ResponseDTO response = gson.fromJson(responseJson, ResponseDTO.class);

            if ("SUCCESS".equals(response.getStatus())) {
                UIUtils.showAlert("Успех", "Билет удален", Alert.AlertType.INFORMATION);
                handleBackButton();
            } else if ("TICKET_NOT_FOUND".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", "Билет с таким ID не найден", Alert.AlertType.ERROR);
                handleBackButton();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось удалить билет: " + response.getMessage(), Alert.AlertType.ERROR);
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
