package Admin.MovieActions;

import Admin.GeneralActions.MovieActionBase;
import Models.Movie;
import Utils.FieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class UpdateMovie extends MovieActionBase {

    @FXML private TextField idField, titleField, genreField, durationField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker releaseDatePicker;
    @FXML private Button backButton;
    @FXML public Button updateButton;

    @FXML
    private void updateMovieAction() {
        if (!FieldValidator.validatePositiveNumericField(idField, "ID фильма должен быть положительным числом.") ||
                !FieldValidator.validateTextField(titleField, "Название фильма должно быть не менее 2 символов.", 2) ||
                !FieldValidator.validateTextField(genreField, "Жанр фильма должен быть не менее 2 символов.", 2) ||
                !FieldValidator.validatePositiveNumericField(durationField, "Продолжительность фильма должна быть положительным числом.") ||
                releaseDatePicker.getValue() == null) {
            return;
        }

        Movie movie = createMovieFromInput();
        Stage stage = (Stage) updateButton.getScene().getWindow();
        sendMovieCommand("MOVIE;UPDATE", movie, stage);
    }

    private Movie createMovieFromInput() {
        Movie movie = new Movie();
        movie.setId(Integer.parseInt(idField.getText()));
        movie.setTitle(titleField.getText());
        movie.setGenre(genreField.getText());
        movie.setDuration(Integer.parseInt(durationField.getText()));

        LocalDate releaseDate = releaseDatePicker.getValue();
        if (releaseDate != null) {
            movie.setReleaseDate(releaseDate.toString());
        }
        movie.setDescription(descriptionField.getText());
        return movie;
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}