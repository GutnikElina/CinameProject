package Utils;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UIUtils {
    private static final Logger LOGGER = Logger.getLogger(UIUtils.class.getName());

    public static HBox createHBox(double spacing, javafx.scene.Node... children) {
        HBox hbox = new HBox(spacing, children);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        return hbox;
    }

    public static PasswordField createPasswordField(String prompt) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(prompt);
        return passwordField;
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
