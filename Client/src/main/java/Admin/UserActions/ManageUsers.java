package Admin.UserActions;

import Elements.Menu.AdminMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import Utils.UIUtils;

public class ManageUsers extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Управление пользователями");

        Label titleLabel = UIUtils.createLabel("Управление пользователями", 24);
        VBox buttonBox = UIUtils.createVBox(15, Pos.CENTER,
                createUserActionButton("Добавить пользователя", "ADD_USER"),
                createUserActionButton("Удалить пользователя", "DELETE_USER"),
                createUserActionButton("Найти пользователя", "GET_USER"),
                createUserActionButton("Вывести всех пользователей", "GET_ALL_USERS"),
                createUserActionButton("Обновить пользователя", "UPDATE_USER")
        );

        Button backButton = UIUtils.createButton("Назад", 300, e -> AdminMenu.show("yourToken"), true);
        VBox vbox = UIUtils.createVBox(20, Pos.CENTER, titleLabel, buttonBox, backButton);

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createUserActionButton(String text, String action) {
        return UIUtils.createButton(text, 300, e -> openUserAction(action), false);
    }

    private void openUserAction(String action) {
        switch (action) {
            case "ADD_USER":
                new AddUser().start(new Stage());
                break;
            case "DELETE_USER":
                new DeleteUser().start(new Stage());
                break;
            case "GET_USER":
                new FindUser().start(new Stage());
                break;
            case "GET_ALL_USERS":
                new GetAllUsers().start(new Stage());
                break;
            case "UPDATE_USER":
                new UpdateUser().start(new Stage());
                break;
            default:
                UIUtils.showAlert("Ошибка", "Неизвестная команда", Alert.AlertType.ERROR);
        }
    }
}