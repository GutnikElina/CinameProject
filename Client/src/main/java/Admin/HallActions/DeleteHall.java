package Admin.HallActions;

import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeleteHall extends Application {

    private TextField idField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Удалить зал");

        idField = UIUtils.createTextField("ID зала");

        VBox vbox = UIUtils.createVBox(15, javafx.geometry.Pos.CENTER, idField,
                UIUtils.createButton("Удалить зал", 200, e -> deleteHallAction(primaryStage), true));
        vbox.setPadding(new javafx.geometry.Insets(15));
        vbox.getStyleClass().add("vbox");

        Scene scene = new Scene(vbox, 350, 220);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deleteHallAction(Stage stage) {
        String hallId = idField.getText().trim();

        if (!FieldValidator.validateNumericField(idField, "Необходимо ввести корректный ID зала")) return;

        String command = "HALL;DELETE;" + hallId;
        String response = AppUtils.sendToServer(command);

        if (response != null && response.contains("HALL_DELETED")) {
            AppUtils.showAlert("Зал удален", "Удаление прошло успешно.", Alert.AlertType.INFORMATION);
            stage.close();
        } else {
            AppUtils.showAlert("Ошибка", "Не удалось удалить зал.", Alert.AlertType.ERROR);
        }
    }

    public static void main(String[] args) { launch(args); }
}
