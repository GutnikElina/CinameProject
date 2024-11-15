package Admin.SessionActions;

import Elements.Menu.AdminMenu;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.UIUtils;

public class ManageSessions extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Управление сеансами");

        Label titleLabel = UIUtils.createLabel("Управление сеансами", 24);
        VBox buttonBox = UIUtils.createVBox(15, Pos.CENTER,
                createSessionActionButton("Добавить сеанс", "ADD_SESSION"),
                createSessionActionButton("Удалить сеанс", "DELETE_SESSION"),
                createSessionActionButton("Найти сеанс", "GET_SESSION"),
                createSessionActionButton("Вывести все сеансы", "GET_ALL_SESSIONS"),
                createSessionActionButton("Обновить сеанс", "UPDATE_SESSION")
        );

        Button backButton = UIUtils.createButton("Назад", 300, e -> AdminMenu.show("yourToken"), true);
        VBox vbox = UIUtils.createVBox(20, Pos.CENTER, titleLabel, buttonBox, backButton);

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createSessionActionButton(String text, String action) {
        return UIUtils.createButton(text, 300, e -> openSessionAction(action), false);
    }

    private void openSessionAction(String action) {
        switch (action) {
            case "ADD_SESSION":
                new AddSession().start(new Stage());
                break;
            case "DELETE_SESSION":
                new DeleteSession().start(new Stage());
                break;
            case "GET_SESSION":
                new FindSession().start(new Stage());
                break;
            case "GET_ALL_SESSIONS":
                new GetAllSessions().start(new Stage());
                break;
            case "UPDATE_SESSION":
                new UpdateSession().start(new Stage());
                break;
            default:
                UIUtils.showAlert("Ошибка", "Неизвестная команда", Alert.AlertType.ERROR);
        }
    }
}
