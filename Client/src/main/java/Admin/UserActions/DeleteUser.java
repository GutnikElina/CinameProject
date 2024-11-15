package Admin.UserActions;

import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class DeleteUser extends UserActionBase {

    private TextField idField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Удалить пользователя");

        idField = UIUtils.createTextField("ID пользователя");

        VBox vbox = UIUtils.createVBox(15, Pos.CENTER, idField,
                UIUtils.createButton("Удалить пользователя", 200, e -> deleteUserAction(primaryStage), true));

        Scene scene = new Scene(vbox, 350, 220);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deleteUserAction(Stage stage) {
        if (!FieldValidator.validateNumericField(idField, "ID пользователя должен быть числом.")) return;

        String command = "USER;DELETE;" + idField.getText();
        sendCommandToServer(command, stage);
    }
}
