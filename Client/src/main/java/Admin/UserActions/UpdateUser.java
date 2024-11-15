package Admin.UserActions;

import Models.User;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpdateUser extends UserActionBase {

    private TextField idField, usernameField, passwordField, roleField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Обновить пользователя");

        idField = UIUtils.createTextField("Введите ID пользователя");
        usernameField = UIUtils.createTextField("Введите имя пользователя");
        passwordField = UIUtils.createTextField("Введите новый пароль");
        roleField = UIUtils.createTextField("Введите роль пользователя");

        VBox vbox = UIUtils.createVBox(15, Pos.CENTER,
                UIUtils.createLabel("Обновление пользователя", 18),
                idField, usernameField, passwordField, roleField,
                UIUtils.createButton("Обновить пользователя", 150, e -> updateUserAction(primaryStage), false));

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateUserAction(Stage stage) {
        boolean valid = true;

        valid &= FieldValidator.validateNumericField(idField, "Пожалуйста, введите корректный ID пользователя.");
        valid &= FieldValidator.validateTextField(usernameField, "Логин пользователя должно быть не короче 3 символов.", 3);
        valid &= FieldValidator.validateTextField(passwordField, "Пароль должен содержать хотя бы 5 символов.", 5);
        valid &= FieldValidator.validateTextField(roleField, "Пожалуйста, укажите роль пользователя.", 1);

        if (valid) {
            User user = createUserFromInput();
            sendUserUpdateCommand(user, stage);
        } else {
            UIUtils.showAlert("Ошибка", "Пожалуйста, исправьте ошибки в форме.", Alert.AlertType.ERROR);
        }
    }

    private User createUserFromInput() {
        User user = new User();
        user.setId(Integer.parseInt(idField.getText()));
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());
        user.setRole(roleField.getText());
        return user;
    }
}