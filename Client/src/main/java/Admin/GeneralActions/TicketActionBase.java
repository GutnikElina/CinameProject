package Admin.GeneralActions;

import Models.Ticket;
import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.Map;

public abstract class TicketActionBase {

    private final Gson gson = GsonFactory.create();

    protected void sendTicketCommand(Ticket ticket, Stage stage) {
        try {
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand("TICKET;UPDATE");
            requestDTO.setData(Map.of(
                    "id", String.valueOf(ticket.getId()),
                    "sessionId", String.valueOf(ticket.getSessionId()),
                    "userId", String.valueOf(ticket.getUserId()),
                    "seatNumber", ticket.getSeatNumber(),
                    "status", ticket.getStatus(),
                    "requestType", ticket.getRequestType(),
                    "purchaseTime", ticket.getPurchaseTime().toString()
            ));
            String requestJson = gson.toJson(requestDTO);
            String responseJson = AppUtils.sendJsonCommandToServer(requestJson);
            ResponseDTO response = gson.fromJson(responseJson, ResponseDTO.class);

            if ("ERROR".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Успешное выполнение", response.getMessage(), Alert.AlertType.INFORMATION);
                if (stage != null) stage.close();
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
