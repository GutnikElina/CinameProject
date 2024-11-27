package Elements.Menu;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EmployeeMenu {

    public static String employeeToken;
    @FXML private Button getAllSessions;
    @FXML private Button findMovie;
    @FXML private Button orderTicket;
    @FXML private Button out;

    public static void show(String token) {
        if (token == null || token.isEmpty()) {
            UIUtils.showAlert("Ошибка", "Необходим токен для доступа!", Alert.AlertType.ERROR);
            return;
        }
        employeeToken = token;
        UIUtils.openNewWindow("/SceneBuilder/EmployeeMenu.fxml", "Меню работника");
    }

    @FXML
    private void initialize() {
        UIUtils.setButtonAction(getAllSessions, "/SceneBuilder/GetAllSessions.fxml", "Просмотр сеансов");
        UIUtils.setButtonAction(findMovie, "/SceneBuilder/FindMovies.fxml", "Информация о фильме");
        UIUtils.setButtonAction(orderTicket, "/SceneBuilder/ConfirmTicket.fxml", "Подтверждение заказов билетов");

        out.setOnAction(e -> {
            Stage stage = (Stage) out.getScene().getWindow();
            UIUtils.closeStageAndShowMainMenu(stage);
        });
    }
}
