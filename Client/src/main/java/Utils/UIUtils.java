package Utils;

import Elements.Menu.MainMenu;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class UIUtils {

    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(Objects.requireNonNull(UIUtils.class.getResource("/style.css")).toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            log.error("Ошибка при загрузке сцены: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить сцену: " + title, Alert.AlertType.ERROR);
        }
    }

    public static void openNewWindow(String fxmlPath, String title) {
        try {
            Stage newStage = new Stage();
            loadScene(newStage, fxmlPath, title);
        } catch (Exception e) {
            log.error("Ошибка при открытии окна: {}", title, e);  // Log error using slf4j
            showAlert("Ошибка", "Не удалось открыть окно: " + title, Alert.AlertType.ERROR);
        }
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void setButtonAction(Button button, String fxmlPath, String title) {
        button.setOnAction(e -> openNewWindow(fxmlPath, title));
    }

    public static void closeStageAndShowMainMenu(Stage stage) {
        stage.close();
        MainMenu.show(new Stage());
    }

    public static void closeCurrentWindow(Stage stage) {
        stage.close();
    }
}
