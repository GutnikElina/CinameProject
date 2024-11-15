package Admin.HallActions;

import Models.Hall;
import Utils.AppUtils;
import Utils.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FindHall extends HallActionBase {

    private TextField hallIdField;
    private Label nameLabel, capacityLabel;
    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Информация о зале");

        HBox inputPanel = createInputPanel();
        GridPane detailsPane = createDetailsPane();
        VBox root = new VBox(20, inputPanel, detailsPane, createBackButton(primaryStage));
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createInputPanel() {
        HBox inputPanel = new HBox(10);
        inputPanel.setAlignment(Pos.CENTER);
        inputPanel.setPadding(new Insets(20));

        hallIdField = UIUtils.createTextField("Введите ID зала");

        Button fetchButton = UIUtils.createButton("Получить данные", 150, e -> fetchHallDetails(), false);

        inputPanel.getChildren().addAll(new Label("ID зала:"), hallIdField, fetchButton);
        return inputPanel;
    }

    private GridPane createDetailsPane() {
        GridPane detailsPane = new GridPane();
        detailsPane.setPadding(new Insets(20));
        detailsPane.setHgap(10);
        detailsPane.setVgap(10);
        detailsPane.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #ddd; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 5, 0, 0, 5);");

        nameLabel = createDetailLabel();
        capacityLabel = createDetailLabel();

        detailsPane.add(new Label("Название зала:"), 0, 0);
        detailsPane.add(nameLabel, 1, 0);
        detailsPane.add(new Label("Вместимость:"), 0, 1);
        detailsPane.add(capacityLabel, 1, 1);

        return detailsPane;
    }

    private Label createDetailLabel() {
        Label label = new Label();
        label.getStyleClass().add("search-label");
        return label;
    }

    private Button createBackButton(Stage primaryStage) {
        backButton = new Button("Назад");
        backButton.setOnAction(e -> primaryStage.close());
        return backButton;
    }

    private void fetchHallDetails() {
        try {
            int hallId = Integer.parseInt(hallIdField.getText());
            sendHallFindRequest(hallId);
        } catch (NumberFormatException e) {
            AppUtils.showAlert("Ошибка", "Введите корректный ID зала!", Alert.AlertType.INFORMATION);
        }
    }

    private void sendHallFindRequest(int hallId) {
        String message = "HALL;GET;" + hallId;
        String response = AppUtils.sendToServer(message);

        if (response.startsWith("HALL_FOUND;")) {
            String[] hallData = response.split(";");
            Hall hall = new Hall();
            hall.setId(Integer.parseInt(hallData[1]));
            hall.setName(hallData[2]);
            hall.setCapacity(Integer.parseInt(hallData[3]));

            updateHallDetails(hall);
        } else {
            AppUtils.showAlert("Ошибка", "Зал не найден.", Alert.AlertType.INFORMATION);
        }
    }

    private void updateHallDetails(Hall hall) {
        nameLabel.setText(hall.getName());
        capacityLabel.setText(String.valueOf(hall.getCapacity()));
    }
}
