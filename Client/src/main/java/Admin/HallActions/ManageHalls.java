package Admin.HallActions;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Utils.UIUtils;

public class ManageHalls {

    @FXML private Button addHalls;
    @FXML private Button deleteHalls;
    @FXML private Button findHalls;
    @FXML private Button getAllHalls;
    @FXML private Button updateHalls;
    @FXML private Button out;

    @FXML
    private void initialize() {
        addHalls.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/AddHall.fxml", "Добавление зала"));
        deleteHalls.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/DeleteHall.fxml", "Удаление зала"));
        findHalls.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/FindHall.fxml", "Поиск зала"));
        getAllHalls.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/GetAllHalls.fxml", "Просмотр всех залов"));
        updateHalls.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/UpdateHall.fxml", "Обновление зала"));
        out.setOnAction(e -> UIUtils.closeCurrentWindow((Stage) out.getScene().getWindow()));
    }
}
