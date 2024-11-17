package Elements.Menu;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuController {
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @FXML
    public void initialize() {
        loginButton.setOnAction(e ->
                UIUtils.openNewWindow("/SceneBuilder/LoginForm.fxml", "Авторизация"));
        registerButton.setOnAction(e ->
                UIUtils.openNewWindow("/SceneBuilder/RegistrationForm.fxml", "Регистрация"));
    }
}
