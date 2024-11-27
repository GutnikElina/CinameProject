package Admin.SessionActions;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageSessions {

    @FXML private Button addSessions;
    @FXML private Button deleteSessions;
    @FXML private Button findSessions;
    @FXML private Button getAllSessions;
    @FXML private Button updateSessions;
    @FXML private Button out;

    @FXML
    private void initialize() {
        addSessions.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/AddSession.fxml", "Добавить сеанс"));
        deleteSessions.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/DeleteSession.fxml", "Удалить сеанс"));
        findSessions.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/FindSession.fxml", "Найти сеанс"));
        getAllSessions.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/GetAllSessions.fxml", "Все сеансы"));
        updateSessions.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/UpdateSession.fxml", "Обновить сеанс"));
        out.setOnAction(e -> UIUtils.closeCurrentWindow((Stage) out.getScene().getWindow()));
    }
}