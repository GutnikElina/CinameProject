package Admin.SessionActions;

import Utils.UIUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Utils.FieldValidator;

public class AddSession extends Application {

    private DatePicker startDatePicker, endDatePicker;
    private TextField startTimeField, endTimeField;
    private ComboBox<String> movieComboBox, hallComboBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Добавить сеанс");

        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();

        startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm");

        endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm");

        movieComboBox = new ComboBox<>();
        hallComboBox = new ComboBox<>();
        loadMoviesAndHalls();

        Button addButton = UIUtils.createButton("Добавить сеанс", 150, e -> addSessionAction(primaryStage), false);
        Button cancelButton = UIUtils.createButton("Отмена", 150, e -> primaryStage.close(), false);
        HBox buttonBox = UIUtils.createHBox(10, addButton, cancelButton);

        GridPane grid = createSessionGrid();

        VBox mainLayout = new VBox(10, grid, buttonBox);
        mainLayout.setPadding(new Insets(20));

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadMoviesAndHalls() {
        movieComboBox.getItems().addAll("Movie 1", "Movie 2");
        hallComboBox.getItems().addAll("Hall 1", "Hall 2");
    }

    private GridPane createSessionGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Дата начала:"), 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(new Label("Дата окончания:"), 0, 1);
        grid.add(endDatePicker, 1, 1);
        grid.add(new Label("Время начала:"), 0, 2);
        grid.add(startTimeField, 1, 2);
        grid.add(new Label("Время окончания:"), 0, 3);
        grid.add(endTimeField, 1, 3);
        grid.add(new Label("Фильм:"), 0, 4);
        grid.add(movieComboBox, 1, 4);
        grid.add(new Label("Зал:"), 0, 5);
        grid.add(hallComboBox, 1, 5);

        return grid;
    }

    private void addSessionAction(Stage primaryStage) {
        if (!FieldValidator.validateDate(startDatePicker, "Выберите правильную дату начала сеанса.") ||
                !FieldValidator.validateDate(endDatePicker, "Выберите правильную дату окончания сеанса.") ||
                !FieldValidator.validateTime(startTimeField, "Введите правильное время начала (HH:mm).") ||
                !FieldValidator.validateTime(endTimeField, "Введите правильное время окончания (HH:mm).")) {
            return;
        }
        UIUtils.showAlert("Успех", "Сеанс успешно добавлен.", Alert.AlertType.INFORMATION);
    }

    public static void main(String[] args) {
        launch(args);
    }
}