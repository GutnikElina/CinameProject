package Admin.MovieActions;

import Models.Movie;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddMovie extends Application {

    private TextField titleField, genreField, durationField, posterField, trailerField;
    private TextArea descriptionField;
    private DatePicker releaseDatePicker;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Добавить фильм");

        titleField = UIUtils.createTextField("Введите название фильма");
        genreField = UIUtils.createTextField("Введите жанр");
        durationField = UIUtils.createTextField("Введите продолжительность");
        posterField = UIUtils.createTextField("Введите URL постера");
        trailerField = UIUtils.createTextField("Введите URL трейлера");
        descriptionField = new TextArea();
        descriptionField.setPromptText("Введите описание фильма");
        descriptionField.setPrefRowCount(4);

        releaseDatePicker = new DatePicker();
        releaseDatePicker.setPromptText("Выберите дату");

        GridPane grid = createGridPane();
        Button addButton = UIUtils.createButton("Добавить фильм", 150, e -> addMovieAction(primaryStage), false);
        Button cancelButton = UIUtils.createButton("Отмена", 150, e -> primaryStage.close(), false);
        HBox buttonBox = UIUtils.createHBox(10, addButton, cancelButton);

        VBox mainLayout = new VBox(10, grid, buttonBox);
        mainLayout.setPadding(new Insets(20));

        Scene scene = new Scene(mainLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Жанр:"), 0, 1);
        grid.add(genreField, 1, 1);
        grid.add(new Label("Продолжительность (мин):"), 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(new Label("Дата выхода:"), 0, 3);
        grid.add(releaseDatePicker, 1, 3);
        grid.add(new Label("URL постера:"), 0, 4);
        grid.add(posterField, 1, 4);
        grid.add(new Label("URL трейлера:"), 0, 5);
        grid.add(trailerField, 1, 5);
        grid.add(new Label("Описание:"), 0, 6);
        grid.add(descriptionField, 1, 6);

        return grid;
    }

    private void addMovieAction(Stage primaryStage) {
        if (!FieldValidator.validateTextField(titleField, "Название фильма обязательно.", 2) ||
                !FieldValidator.validateTextField(genreField, "Жанр фильма обязателен.", 2) ||
                !FieldValidator.validatePositiveNumericField(durationField, "Продолжительность должна быть положительным числом.") ||
                releaseDatePicker.getValue() == null ||
                !FieldValidator.validateUrlField(posterField, "Введите корректный URL постера.") ||
                !FieldValidator.validateUrlField(trailerField, "Введите корректный URL трейлера.")) {
            return;
        }

        Movie movie = createMovieFromInput();
        String response = AppUtils.sendToServer(createMovieCommand("ADD", movie));

        if ("MOVIE_ADDED".equals(response)) {
            UIUtils.showAlert("Успех", "Фильм успешно добавлен.", Alert.AlertType.INFORMATION);
            primaryStage.close();
        } else {
            UIUtils.showAlert("Ошибка", "Ошибка при добавлении фильма.", Alert.AlertType.ERROR);
        }
    }

    private Movie createMovieFromInput() {
        Movie movie = new Movie();
        movie.setTitle(titleField.getText());
        movie.setGenre(genreField.getText());
        movie.setDuration(Integer.parseInt(durationField.getText()));

        LocalDate releaseDate = releaseDatePicker.getValue();
        if (releaseDate != null) {
            movie.setReleaseDate(releaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        movie.setPoster(posterField.getText());
        movie.setTrailerUrl(trailerField.getText());
        movie.setDescription(descriptionField.getText());
        return movie;
    }

    private String createMovieCommand(String action, Movie movie) {
        String releaseDate = movie.getReleaseDate() == null ? "" : movie.getReleaseDate();
        String poster = movie.getPoster() == null ? "" : movie.getPoster();
        String trailerUrl = movie.getTrailerUrl() == null ? "" : movie.getTrailerUrl();
        String description = movie.getDescription() == null ? "" : movie.getDescription();

        return String.join(";", "MOVIE", action, movie.getTitle(), movie.getGenre(),
                String.valueOf(movie.getDuration()), releaseDate, poster, trailerUrl, description);
    }

    public static void main(String[] args) {
        launch(args);
    }
}