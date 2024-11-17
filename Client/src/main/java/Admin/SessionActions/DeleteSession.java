package Admin.SessionActions;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class DeleteSession extends Application {

    private TextField sessionIdField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Удалить сеанс");

        sessionIdField = new TextField();
        sessionIdField.setPromptText("Введите ID сеанса");

        Button deleteButton = new Button("Удалить сеанс");
        deleteButton.setOnAction(e -> deleteSessionAction(primaryStage));

        VBox vbox = new VBox(10, sessionIdField, deleteButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(vbox, 300, 200);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deleteSessionAction(Stage stage) {
        try {
            String sessionIdStr = sessionIdField.getText();

            if (sessionIdStr == null || sessionIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("ID сеанса не должен быть пустым");
            }

            int sessionId = Integer.parseInt(sessionIdStr);

            String deleteSessionCommand = String.format("SESSION;DELETE;%d", sessionId);
            String response = AppUtils.sendToServer(deleteSessionCommand);

            if (response.startsWith("SUCCESS")) {
                UIUtils.showAlert("Успех", "Сеанс удален", Alert.AlertType.INFORMATION);
                stage.close();
            } else if (response.equals("SESSION_NOT_FOUND")) {
                UIUtils.showAlert("Ошибка", "Сеанс с таким ID не найден", Alert.AlertType.ERROR);
                stage.close();
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось удалить сеанс: " + response, Alert.AlertType.ERROR);
                stage.close();
            }

        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "ID сеанса должен быть числом", Alert.AlertType.ERROR);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось удалить сеанс: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
