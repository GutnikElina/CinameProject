package Elements.Menu;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Utils.AppUtils;

public class EmployeeMenu {

    public static String employeeToken;

    @FXML
    private Button getAllSessions;
    @FXML
    private Button findMovie;
    @FXML
    private Button orderTicket;
    @FXML
    private Button out;

    public static void show(String token) {
        if (token == null || token.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Необходим токен для доступа!", Alert.AlertType.ERROR);
            return;
        }
        employeeToken = token;
        UIUtils.loadScene(new Stage(), "/SceneBuilder/EmployeeMenu.fxml", "Меню работника");
    }

    @FXML
    private void initialize() {
        setButtonAction(getAllSessions, "/SceneBuilder/GetAllSessions.fxml", "Просмотр сеансов");
        setButtonAction(findMovie, "/SceneBuilder/FindMovies.fxml", "Информация о фильме");
        setButtonAction(orderTicket, "/SceneBuilder/ConfirmTicket.fxml", "Подтверждение заказов билетов");
        //setButtonAction(orderTicket, "/SceneBuilder/CancelTicket.fxml", "Подтверждение отмены билетов");
        //setButtonAction(orderTicket, "/SceneBuilder/ChangeTicket.fxml", "Подтверждение обмена билетов");

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
//                createNavigationButton("Редактирование своего аккаунта", .class),