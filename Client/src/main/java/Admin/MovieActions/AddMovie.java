package Admin.MovieActions;

import Admin.GeneralActions.MovieActionBase;
import Models.Movie;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class AddMovie extends MovieActionBase {
    @FXML private TextField titleField, genreField, durationField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker releaseDatePicker;
    @FXML private Button backButton;

    @FXML
    private void addMovieAction() {
        boolean valid = true;

        valid &= FieldValidator.validateTextField(titleField, "Название фильма обязательно.", 2);
        valid &= FieldValidator.validateTextField(genreField, "Жанр фильма обязателен.", 2);
        valid &= FieldValidator.validatePositiveNumericField(durationField, "Продолжительность должна быть положительным числом.");
        if (releaseDatePicker.getValue() == null) {
            UIUtils.showAlert("Ошибка", "Пожалуйста, выберите дату выхода фильма.", Alert.AlertType.ERROR);
            valid = false;
        }
        if (!valid) return;

        Movie movie = createMovieFromInput();
        Stage stage = (Stage) titleField.getScene().getWindow();
        sendMovieCommand("MOVIE;ADD;", movie, stage);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private Movie createMovieFromInput() {
        Movie movie = new Movie();
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
}