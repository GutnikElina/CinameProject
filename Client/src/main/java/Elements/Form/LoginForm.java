package Elements.Form;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import MainApp.MainApp;

public class LoginForm extends BaseForm {

    @Override
    protected String getTitle() {
        return "АВТОРИЗАЦИЯ";
    }

    @Override
    protected void configureForm(VBox vbox, Stage stage) {
        TextField usernameField = UIUtils.createTextField("Логин");
        PasswordField passwordField = UIUtils.createPasswordField("Пароль");

        Button loginButton = UIUtils.createButton("Войти", 200, e -> login(usernameField.getText(), passwordField.getText()), false);
        Button backButton = UIUtils.createButton("Назад", 200, e -> {
            stage.close();
            MainApp.getInstance().showMainMenu(stage);
        }, true);

        vbox.getChildren().addAll(usernameField, passwordField, loginButton, backButton);
    }

    private void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            AppUtils.showAlert("Ошибка", "Логин и пароль не должны быть пустыми.", Alert.AlertType.ERROR);
            return;
        }
        MainApp.getInstance().getServerConnection().sendMessage("LOGIN;" + username + ";" + password);
    }
}