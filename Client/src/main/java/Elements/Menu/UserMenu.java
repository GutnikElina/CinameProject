package Elements.Menu;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UserMenu {

    public static String userToken;
    @FXML private Button getAllSessions;
    @FXML private Button buyTicket;
    @FXML private Button findMovie;
    @FXML private Button out;

    public static void show(String token) {
        if (token == null || token.isEmpty()) {
            UIUtils.showAlert("Ошибка", "Необходим токен для доступа", Alert.AlertType.ERROR);
            return;
        }
        userToken = token;
        UIUtils.openNewWindow("/SceneBuilder/UserMenu.fxml", "Меню пользователя");
    }

    @FXML
    private void initialize() {
        UIUtils.setButtonAction(getAllSessions, "/SceneBuilder/GetAllSessions.fxml", "Просмотр сеансов");
        UIUtils.setButtonAction(findMovie, "/SceneBuilder/FindMovie.fxml", "Информация о фильме");
        UIUtils.setButtonAction(buyTicket, "/SceneBuilder/BuyTicket.fxml", "Покупка билета");

        out.setOnAction(e -> {
            Stage stage = (Stage) out.getScene().getWindow();
            UIUtils.closeStageAndShowMainMenu(stage);
        });
    }
}