package Utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIUtils {
    private static final Logger LOGGER = Logger.getLogger(UIUtils.class.getName());

    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(UIUtils.class.getResource("/style.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Ошибка при загрузке сцены: " + e.getMessage());
            e.printStackTrace();
            AppUtils.showAlert("Ошибка", "Не удалось загрузить сцену: " + title, Alert.AlertType.ERROR);
        }
    }

    public static void openNewWindow(String fxmlPath, String title) {
        try {
            Stage newStage = new Stage();
            loadScene(newStage, fxmlPath, title);
        } catch (Exception e) {
            AppUtils.showAlert("Ошибка", "Не удалось открыть окно: " + title, Alert.AlertType.ERROR);
        }
    }

    public static HBox createHBox(double spacing, javafx.scene.Node... children) {
        HBox hbox = new HBox(spacing, children);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        return hbox;
    }

    public static Button createButton(String text, int minWidth, javafx.event.EventHandler<javafx.event.ActionEvent> action, boolean isDanger) {
        Button button = new Button(text);
        button.setMinWidth(minWidth);
        button.setOnAction(action);
        if (isDanger) button.getStyleClass().add("button-danger");
        return button;
    }

    public static Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", fontSize));
        label.setTextFill(Color.web("#2C3E50"));
        return label;
    }

    public static VBox createVBox(double spacing, Pos alignment, javafx.scene.Node... children) {
        VBox vbox = new VBox(spacing, children);
        vbox.setAlignment(alignment);
        return vbox;
    }

    public static TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        return textField;
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void logError(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }
}
