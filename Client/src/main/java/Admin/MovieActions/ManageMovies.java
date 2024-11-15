package Admin.MovieActions;

import Elements.Menu.AdminMenu;
import Utils.AppUtils;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageMovies extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Управление фильмами");

        Label titleLabel = UIUtils.createLabel("Управление фильмами", 24);

        VBox buttonBox = new VBox(15,
                createActionButton("Добавить фильм", "ADD_MOVIE"),
                createActionButton("Удалить фильм", "DELETE_MOVIE"),
                createActionButton( "Найти фильм","FIND_MOVIE"),
                createActionButton("Вывести все фильмы", "GET_ALL_MOVIES"),
                createActionButton("Обновить фильм", "UPDATE_MOVIE")
        );
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = UIUtils.createButton("Назад", 150, e -> {
            primaryStage.close();
            AdminMenu.show("yourToken");
        }, true);

        VBox vbox = new VBox(20, titleLabel, buttonBox, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add(ManageMovies.class.getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createActionButton(String text, String actionCommand) {
        return UIUtils.createButton(text, 300, e -> openMovieActionWindow(actionCommand), false);
    }

    private void openMovieActionWindow(String actionCommand) {
        try {
            switch (actionCommand) {
                case "ADD_MOVIE":
                    new AddMovie().start(new Stage());
                    break;
                case "DELETE_MOVIE":
                    new DeleteMovie().start(new Stage());
                    break;
                case "GET_ALL_MOVIES":
                    new GetAllMovies().start(new Stage());
                    break;
                case "UPDATE_MOVIE":
                    new UpdateMovie().start(new Stage());
                    break;
                case "FIND_MOVIE":
                    new FindMovie().start(new Stage());
                    break;
                default:
                    AppUtils.showAlert("Ошибка", "Неизвестная команда для действия с фильмами.", Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            AppUtils.showAlert("Ошибка", "Ошибка при открытии окна действия с фильмами.", Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }
}
