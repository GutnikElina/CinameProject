package Admin.HallActions;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Utils.UIUtils;

public class ManageHalls {

    @FXML
    private Button addHalls;
    @FXML
    private Button deleteHalls;
    @FXML
    private Button findHalls;
    @FXML
    private Button getAllHalls;
    @FXML
    private Button updateHalls;
    @FXML
    private Button out;

    @FXML
    private void initialize() {
        addHalls.setOnAction(e -> openAction("ADD"));
        deleteHalls.setOnAction(e -> openAction("DELETE"));
        findHalls.setOnAction(e -> openAction("GET"));
        getAllHalls.setOnAction(e -> openAction("GET_ALL"));
        updateHalls.setOnAction(e -> openAction("UPDATE"));
        out.setOnAction(e -> closeWindow());
    }

    private void openAction(String action) {
        switch (action) {
            case "ADD":
                UIUtils.openNewWindow("/SceneBuilder/AddHall.fxml", "Добавление зала");
                break;
            case "DELETE":
                UIUtils.openNewWindow("/SceneBuilder/DeleteHall.fxml", "Удаление зала");
                break;
            case "GET":
                UIUtils.openNewWindow("/SceneBuilder/FindHall.fxml", "Поиск зала");
                break;
            case "GET_ALL":
                UIUtils.openNewWindow("/SceneBuilder/GetAllHalls.fxml", "Просмотр всех залов");
                break;
            case "UPDATE":
                UIUtils.openNewWindow("/SceneBuilder/UpdateHall.fxml", "Обновление зала");
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
