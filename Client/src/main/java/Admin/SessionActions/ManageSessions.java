package Admin.SessionActions;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageSessions {

    @FXML
    private Button addSessions;
    @FXML
    private Button deleteSessions;
    @FXML
    private Button findSessions;
    @FXML
    private Button getAllSessions;
    @FXML
    private Button updateSessions;
    @FXML
    private Button out;

    @FXML
    private void initialize() {
        addSessions.setOnAction(e -> openAction("ADD"));
        deleteSessions.setOnAction(e -> openAction("DELETE"));
        findSessions.setOnAction(e -> openAction("GET"));
        getAllSessions.setOnAction(e -> openAction("GET_ALL"));
        updateSessions.setOnAction(e -> openAction("UPDATE"));
        out.setOnAction(e -> closeWindow());
    }

    private void openAction(String action) {
        try {
            switch (action) {
//                case "ADD":
//                    UIUtils.openNewWindow("/SceneBuilder/AddSession.fxml", "Добавить сеанс");
//                    break;
//                case "DELETE":
//                    UIUtils.openNewWindow("/SceneBuilder/DeleteSession.fxml", "Удалить сеанс");
//                    break;
//                case "GET":
//                    UIUtils.openNewWindow("/SceneBuilder/FindSession.fxml", "Найти сеанс");
//                    break;
                case "GET_ALL":
                    UIUtils.openNewWindow("/SceneBuilder/GetAllSessions.fxml", "Все сеансы");
                    break;
//                case "UPDATE":
//                    UIUtils.openNewWindow("/SceneBuilder/UpdateSession.fxml", "Обновить сеанс");
//                    break;
                default:
                    UIUtils.showAlert("Ошибка", "Неизвестная команда", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при открытии окна: " + action, Alert.AlertType.ERROR);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) out.getScene().getWindow();
        stage.close();
    }
}