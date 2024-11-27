package Admin.TicketActions;

import Utils.AppUtils;
import Utils.UIUtils;
import Models.RequestDTO;
import Models.ResponseDTO;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class FindTicket {

    @FXML private TextField idField;
    @FXML private Label sessionIdLabel, seatNumberLabel, userIdLabel, priceLabel, purchaseTimeLabel;
    @FXML public Button backButton;
    private final Gson gson = new Gson();

    @FXML
    private void searchTicketById() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            UIUtils.showAlert("Ошибка", "Поле ID не может быть пустым.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int ticketId = Integer.parseInt(idText);

            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand("TICKET;GET");
            requestDTO.setData(Map.of("ticketId", String.valueOf(ticketId)));
            String requestJson = gson.toJson(requestDTO);
            String responseJson = AppUtils.sendJsonCommandToServer(requestJson);
            ResponseDTO response = gson.fromJson(responseJson, ResponseDTO.class);
            if ("SUCCESS".equals(response.getStatus())) {
                Map<String, Object> ticketData = (Map<String, Object>) response.getData().get("ticket");
                sessionIdLabel.setText(String.valueOf(ticketData.get("sessionId")));
                seatNumberLabel.setText(ticketData.get("seatNumber") + " место");
                userIdLabel.setText(String.valueOf(ticketData.get("userId")));
                priceLabel.setText(ticketData.get("price") + " рублей");
                purchaseTimeLabel.setText(formatDateTime(String.valueOf(ticketData.get("purchaseTime"))));
            } else if ("TICKET_NOT_FOUND".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", "Билет с указанным ID не найден.", Alert.AlertType.WARNING);
            } else if ("ERROR".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "ID должен быть числом.", Alert.AlertType.WARNING);
            e.printStackTrace();
        } catch (ClassCastException e) {
            UIUtils.showAlert("Ошибка", "Некорректный формат данных билета.", Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Произошла ошибка: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private String formatDateTime(String dateTime) {
        try {
            return dateTime;
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
