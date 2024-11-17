package Elements.Menu;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Utils.AppUtils;

public class AdminMenu {

    public static String adminToken;
    @FXML
    private Button manageFilms;
    @FXML
    private Button manageUsers;
    @FXML
    private Button manageHalls;
    @FXML
    private Button manageSessions;
    @FXML
    private Button manageTickets;
    @FXML
    private Button out;

    public static void show(String token) {
        if (token == null || token.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Необходим токен для доступа", Alert.AlertType.ERROR);
            return;
        }
        adminToken = token;
        UIUtils.loadScene(new Stage(), "/SceneBuilder/AdminMenu.fxml", "Меню администратора");
    }

    @FXML
    private void initialize() {
        manageFilms.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/ManageMovies.fxml", "Управление фильмами"));
        manageUsers.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/ManageUsers.fxml", "Управление пользователями"));
        manageHalls.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/ManageHalls.fxml", "Управление залами"));
        manageSessions.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/ManageSessions.fxml", "Управление сеансами"));
        manageTickets.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/ManageTickets.fxml", "Управление билетами"));

        out.setOnAction(e -> {
            Stage stage = (Stage) out.getScene().getWindow();
            stage.close();
            MainMenu.show(new Stage());
        });
    }
}
