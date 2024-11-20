package Admin.UserActions;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageUsers {

    @FXML private Button addUsers;
    @FXML private Button deleteUsers;
    @FXML private Button findUsers;
    @FXML private Button getAllUsers;
    @FXML private Button updateUsers;
    @FXML private Button out;

    @FXML
    private void initialize() {
        addUsers.setOnAction(e -> openAction("ADD"));
        deleteUsers.setOnAction(e -> openAction("DELETE"));
        findUsers.setOnAction(e -> openAction("GET"));
        getAllUsers.setOnAction(e -> openAction("GET_ALL"));
        updateUsers.setOnAction(e -> openAction("UPDATE"));
        out.setOnAction(e -> closeWindow());
    }

    private void openAction(String action) {
        switch (action) {
            case "ADD":
                UIUtils.openNewWindow("/SceneBuilder/AddUser.fxml", "Добавление пользователей");
                break;
            case "DELETE":
                UIUtils.openNewWindow("/SceneBuilder/DeleteUser.fxml", "Удаление пользователей");
                break;
            case "GET":
                UIUtils.openNewWindow("/SceneBuilder/FindUser.fxml", "Поиск пользователей");
                break;
            case "GET_ALL":
                UIUtils.openNewWindow("/SceneBuilder/GetAllUsers.fxml", "Просмотр всех пользователей");
                break;
            case "UPDATE":
                UIUtils.openNewWindow("/SceneBuilder/UpdateUser.fxml", "Обновление пользователей");
                break;
            default:
                UIUtils.showAlert("Ошибка", "Неизвестная команда", Alert.AlertType.ERROR);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) out.getScene().getWindow();
        stage.close();
    }
}

