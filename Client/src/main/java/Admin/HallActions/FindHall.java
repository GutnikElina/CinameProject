package Admin.HallActions;

import Admin.GeneralActions.HallActionBase;
import Admin.GeneralActions.UserActionBase;
import Models.Hall;
import Models.User;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class FindHall extends HallActionBase {

    @FXML
    private TextField hallIdField;
    @FXML
    private Label nameLabel, capacityLabel;
    @FXML
    private Button backButton;

    @FXML
    private void fetchHallDetails() {
        try {
            int hallId = Integer.parseInt(hallIdField.getText());
            sendHallFindRequest(hallId);
        } catch (NumberFormatException e) {
            AppUtils.showAlert("Ошибка", "Введите корректный ID зала!", Alert.AlertType.INFORMATION);
        }
    }

    private void sendHallFindRequest(int hallId) {
        String message = "HALL;GET;" + hallId;
        String response = AppUtils.sendToServer(message);

        if (response.startsWith("HALL_FOUND;")) {
            String[] hallData = response.split(";");
            Hall hall = new Hall();
            hall.setId(Integer.parseInt(hallData[1]));
            hall.setName(hallData[2]);
            hall.setCapacity(Integer.parseInt(hallData[3]));

            updateHallDetails(hall);
        } else {
            AppUtils.showAlert("Ошибка", "Зал не найден.", Alert.AlertType.INFORMATION);
        }
    }

    private void updateHallDetails(Hall hall) {
        nameLabel.setText(hall.getName());
        capacityLabel.setText(String.valueOf(hall.getCapacity()));
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}

