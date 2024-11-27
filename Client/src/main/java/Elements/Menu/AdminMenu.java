package Elements.Menu;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminMenu {

    public static String adminToken;
    @FXML private Button manageFilms;
    @FXML private Button manageUsers;
    @FXML private Button manageHalls;
    @FXML private Button manageSessions;
    @FXML private Button manageTickets;
    @FXML private Button out;

    public static void show(String token) {
        if (token == null || token.isEmpty()) {
            UIUtils.showAlert("Ошибка", "Необходим токен для доступа", Alert.AlertType.ERROR);
            return;
        }
        adminToken = token;
        UIUtils.openNewWindow("/SceneBuilder/AdminMenu.fxml", "Меню администратора");
    }

    @FXML
    private void initialize() {
        UIUtils.setButtonAction(manageFilms, "/SceneBuilder/ManageMovies.fxml", "Управление фильмами");
        UIUtils.setButtonAction(manageUsers, "/SceneBuilder/ManageUsers.fxml", "Управление пользователями");
        UIUtils.setButtonAction(manageHalls, "/SceneBuilder/ManageHalls.fxml", "Управление залами");
        UIUtils.setButtonAction(manageSessions, "/SceneBuilder/ManageSessions.fxml", "Управление сеансами");
        UIUtils.setButtonAction(manageTickets, "/SceneBuilder/ManageTickets.fxml", "Управление билетами");

        out.setOnAction(e -> {
            Stage stage = (Stage) out.getScene().getWindow();
            UIUtils.closeStageAndShowMainMenu(stage);
        });
    }
}