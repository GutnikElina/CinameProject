package Admin.MovieActions;

import Models.Movie;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateMovie {
    @FXML
    private TextField idField, titleField, genreField, durationField, posterField, trailerField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private Button backButton;
    @FXML
    public Button updateButton;

    @FXML
    private void updateMovieAction() {
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
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
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

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
