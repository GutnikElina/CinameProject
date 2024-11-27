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
        addUsers.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/AddUser.fxml", "Добавление пользователей"));
        deleteUsers.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/DeleteUser.fxml", "Удаление пользователей"));
        findUsers.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/FindUser.fxml", "Поиск пользователей"));
        getAllUsers.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/GetAllUsers.fxml", "Просмотр всех пользователей"));
        updateUsers.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/UpdateUser.fxml", "Обновление пользователей"));
        out.setOnAction(e -> UIUtils.closeCurrentWindow((Stage) out.getScene().getWindow()));
    }
}

