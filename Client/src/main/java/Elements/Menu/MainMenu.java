package Elements.Menu;

import Elements.Form.LoginForm;
import Elements.Form.RegistrationForm;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.UIUtils;

public class MainMenu {

    public static void show(Stage stage) {
        Stage finalStage = (stage == null) ? new Stage() : stage;

        finalStage.setTitle("Авторизация/Регистрация");

        Label title = UIUtils.createLabel("ДОБРО ПОЖАЛОВАТЬ!", 36);
        Button loginButton = UIUtils.createButton("Авторизация", 300, e -> new LoginForm().show(finalStage), false);
        Button registerButton = UIUtils.createButton("Регистрация", 300, e -> new RegistrationForm().show(finalStage), false);

        VBox vbox = UIUtils.createVBox(30, Pos.CENTER, title, loginButton, registerButton);
        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        finalStage.setScene(scene);
        finalStage.show();
    }
}
