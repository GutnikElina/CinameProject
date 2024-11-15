package Admin.UserActions;

import Models.User;
import Utils.AppUtils;
import Utils.UIUtils;
import Utils.FieldValidator;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class FindUser extends UserActionBase {

    private TextField userIdField;
    private Label usernameLabel, roleLabel, createdAtLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Информация о пользователе");

        userIdField = UIUtils.createTextField("Введите ID пользователя");
        Button fetchButton = UIUtils.createButton("Получить данные", 150, e -> fetchUserDetails(), false);

        usernameLabel = createDetailLabel();
        roleLabel = createDetailLabel();
        createdAtLabel = createDetailLabel();

        GridPane detailsPane = new GridPane();
        detailsPane.setVgap(10);
        detailsPane.add(new Label("Имя пользователя:"), 0, 0);
        detailsPane.add(usernameLabel, 1, 0);
        detailsPane.add(new Label("Роль:"), 0, 1);
        detailsPane.add(roleLabel, 1, 1);
        detailsPane.add(new Label("Дата создания:"), 0, 2);
        detailsPane.add(createdAtLabel, 1, 2);

        VBox root = new VBox(20, userIdField, fetchButton, detailsPane);
        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchUserDetails() {
        if (!FieldValidator.validateNumericField(userIdField, "Введите корректный ID пользователя!")) {
            return;
        }

        int userId = Integer.parseInt(userIdField.getText());
        String response = AppUtils.sendToServer("USER;GET;" + userId);

        if (response.startsWith("USER_FOUND;")) {
            String[] userData = response.split(";");
            User user = new User();
            user.setId(Integer.parseInt(userData[1]));
            user.setUsername(userData[2]);
            user.setRole(userData[3]);
            user.setCreatedAt(LocalDateTime.parse(userData[4]));
            updateUserDetails(user);
        } else {
            UIUtils.showAlert("Ошибка", "Пользователь не найден.", Alert.AlertType.INFORMATION);
        }
    }

    private void updateUserDetails(User user) {
        usernameLabel.setText(user.getUsername());
        roleLabel.setText(user.getRole());
        createdAtLabel.setText(user.getCreatedAt().toString());
    }

    private Label createDetailLabel() {
        Label label = new Label();
        label.getStyleClass().add("search-label");
        return label;
    }
}