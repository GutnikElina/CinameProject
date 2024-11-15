package Elements.Form;

import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import MainApp.MainApp;

public class RegistrationForm extends BaseForm {

    @Override
    protected String getTitle() {
        return "РЕГИСТРАЦИЯ";
    }

    @Override
    protected void configureForm(VBox vbox, Stage stage) {
        TextField usernameField = UIUtils.createTextField("Логин");
        PasswordField passwordField = UIUtils.createPasswordField("Пароль");

        Button registerButton = createButton("Зарегистрироваться", e -> register(usernameField, passwordField, stage));
        Button backButton = createButton("Назад", e -> stage.close());

        vbox.getChildren().addAll(usernameField, passwordField, registerButton, backButton);
    }

    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler) {
        return UIUtils.createButton(text, 200, eventHandler, false);
    }

    private void register(TextField usernameField, PasswordField passwordField, Stage stage) {
        resetFieldStyles(usernameField, passwordField);

        boolean isValid = true;
        isValid &= FieldValidator.validateTextField(usernameField, "Логин должен содержать минимум 3 символа.", 3);
        isValid &= FieldValidator.validateTextField(passwordField, "Пароль должен содержать минимум 5 символов.", 5);

        if (isValid) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            MainApp.getInstance().getServerConnection().sendMessage("REGISTER;" + username + ";" + password);
        }
    }

    private void resetFieldStyles(TextField usernameField, PasswordField passwordField) {
        usernameField.setStyle("-fx-border-color: none;");
        passwordField.setStyle("-fx-border-color: none;");
    }
}
