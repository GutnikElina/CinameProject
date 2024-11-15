package Admin.UserActions;

import Models.User;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class AddUser extends UserActionBase {

    private TextField usernameField, passwordField, roleField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Добавить пользователя");

        usernameField = UIUtils.createTextField("Введите логин пользователя");
        passwordField = UIUtils.createTextField("Введите пароль");
        roleField = UIUtils.createTextField("Введите роль пользователя");

        VBox vbox = UIUtils.createVBox(15, Pos.CENTER,
                UIUtils.createLabel("Добавление нового пользователя", 18),
                usernameField, passwordField, roleField,
                UIUtils.createButton("Добавить пользователя", 150, e -> addUserAction(primaryStage), false));

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addUserAction(Stage stage) {
        boolean valid = true;

        valid &= FieldValidator.validateTextField(usernameField, "Логин должен содержать не менее 3 символов.", 3);
        valid &= FieldValidator.validateTextField(passwordField, "Пароль должен содержать не менее 5 символов.", 5);
        valid &= FieldValidator.validateTextField(roleField, "Роль не может быть пустой.", 1);

        if (!valid) return;

        User user = createUser(usernameField.getText(), passwordField.getText(), roleField.getText());
        sendUserCommand(user, stage);
    }
}
