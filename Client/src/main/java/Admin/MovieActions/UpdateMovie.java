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

public class UpdateMovie extends Application {

    private TextField idField, titleField, genreField, durationField, posterField, trailerField;
    private TextArea descriptionField;
    private DatePicker releaseDatePicker;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Обновить фильм");

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));

        idField = UIUtils.createTextField("Введите ID фильма");
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

        grid.add(new Label("ID фильма:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Название:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Жанр:"), 0, 2);
        grid.add(genreField, 1, 2);
        grid.add(new Label("Продолжительность (мин):"), 0, 3);
        grid.add(durationField, 1, 3);
        grid.add(new Label("Дата выхода:"), 0, 4);
        grid.add(releaseDatePicker, 1, 4);
        grid.add(new Label("URL постера:"), 0, 5);
        grid.add(posterField, 1, 5);
        grid.add(new Label("URL трейлера:"), 0, 6);
        grid.add(trailerField, 1, 6);
        grid.add(new Label("Описание:"), 0, 7);
        grid.add(descriptionField, 1, 7);

        Button updateButton = new Button("Обновить фильм");
        updateButton.getStyleClass().add("button-primary");
        updateButton.setOnAction(e -> updateMovieAction(primaryStage));

        Button cancelButton = new Button("Отмена");
        cancelButton.getStyleClass().add("button-secondary");
        cancelButton.setOnAction(e -> primaryStage.close());

        HBox buttonBox = new HBox(15, updateButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        VBox mainLayout = new VBox(10, grid, buttonBox);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getStyleClass().add("main-layout");

        Scene scene = new Scene(mainLayout, 800, 650);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateMovieAction(Stage primaryStage) {
        if (!FieldValidator.validatePositiveNumericField(idField, "ID фильма должен быть положительным числом.") ||
                !FieldValidator.validateTextField(titleField, "Название фильма должно быть не менее 2 символов.", 2) ||
                !FieldValidator.validateTextField(genreField, "Жанр фильма должен быть не менее 2 символов.", 2) ||
                !FieldValidator.validatePositiveNumericField(durationField, "Продолжительность фильма должна быть положительным числом.") ||
                releaseDatePicker.getValue() == null ||
                !FieldValidator.validateUrlField(posterField, "Введите корректный URL постера.") ||
                !FieldValidator.validateUrlField(trailerField, "Введите корректный URL трейлера.")) {
            return;
        }

        Movie movie = createMovieFromInput();
        String response = AppUtils.sendToServer(constructUpdateCommand(movie));

        if ("MOVIE_UPDATED".equals(response)) {
            UIUtils.showAlert("Фильм обновлен!", "Фильм был успешно обновлен.", Alert.AlertType.INFORMATION);
            primaryStage.close();
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось обновить фильм.", Alert.AlertType.ERROR);
        }
    }

    private Movie createMovieFromInput() {
        Movie movie = new Movie();
        movie.setId(Integer.parseInt(idField.getText()));
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

    private String constructUpdateCommand(Movie movie) {
        return "MOVIE;UPDATE;" + movie.getId() + ";" + movie.getTitle() + ";" +
                movie.getGenre() + ";" + movie.getDuration() + ";" +
                (movie.getReleaseDate() != null ? movie.getReleaseDate() : "") + ";" +
                (movie.getPoster() != null ? movie.getPoster() : "") + ";" +
                (movie.getTrailerUrl() != null ? movie.getTrailerUrl() : "") + ";" +
                (movie.getDescription() != null ? movie.getDescription() : "");
    }

    public static void main(String[] args) {
        launch(args);
    }
}