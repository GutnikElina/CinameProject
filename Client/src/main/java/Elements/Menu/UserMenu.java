package Elements.Menu;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Utils.AppUtils;

public class UserMenu {

    public static String userToken;

    @FXML
    private Button getAllSessions;
    @FXML
    private Button findMovie;
    @FXML
    private Button out;

    public static void show(String token) {
        if (token == null || token.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Необходим токен для доступа", Alert.AlertType.ERROR);
            return;
        }
        userToken = token;
        UIUtils.loadScene(new Stage(), "/SceneBuilder/UserMenu.fxml", "Меню пользователя");
    }

    @FXML
    private void initialize() {
        setButtonAction(getAllSessions, "/SceneBuilder/GetAllSessions.fxml", "Просмотр сеансов");
        setButtonAction(findMovie, "/SceneBuilder/FindMovie.fxml", "Информация о фильме");

        out.setOnAction(e -> {
            closeStageAndShowMainMenu();
        });
    }

    private void setButtonAction(Button button, String fxmlPath, String title) {
        button.setOnAction(e -> UIUtils.openNewWindow(fxmlPath, title));
    }

    private void closeStageAndShowMainMenu() {
        Stage stage = (Stage) out.getScene().getWindow();
        stage.close();
        MainMenu.show(new Stage());
    }
}
//                createNavigationButton("Покупка билетов", .class),
//                createNavigationButton("Редактирование своего аккаунта", .class),
//                createNavigationButton("Отмена билетов", .class),
//                createNavigationButton("Обмен билетов", .class),