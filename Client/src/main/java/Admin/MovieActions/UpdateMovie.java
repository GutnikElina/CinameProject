package Admin.MovieActions;

import Admin.GeneralActions.MovieActionBase;
import Models.Movie;
import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Map;

public class UpdateMovie extends MovieActionBase {

    @FXML private TextField idField, titleField, genreField, durationField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker releaseDatePicker;
    @FXML private Button backButton;
    @FXML public Button updateButton;
    private final Gson gson = GsonFactory.create();

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
        System.out.println(movie);
        Stage stage = (Stage) updateButton.getScene().getWindow();

        try {
            RequestDTO requestDTO = new RequestDTO();
            requestDTO.setCommand("MOVIE;UPDATE;");
            requestDTO.setData(Map.of(
                    "id", String.valueOf(movie.getId()),
                    "title", movie.getTitle(),
                    "genre", movie.getGenre(),
                    "duration", String.valueOf(movie.getDuration()),
                    "releaseDate", movie.getReleaseDate(),
                    "description", movie.getDescription()
            ));

            String requestJson = gson.toJson(requestDTO);
            String responseJson = AppUtils.sendJsonCommandToServer(requestJson);
            ResponseDTO response = gson.fromJson(responseJson, ResponseDTO.class);

            if ("ERROR".equals(response.getStatus())) {
                UIUtils.showAlert("Ошибка", response.getMessage(), Alert.AlertType.ERROR);
            } else {
                UIUtils.showAlert("Успешное выполнение", response.getMessage(), Alert.AlertType.INFORMATION);
                if (stage != null) stage.close();
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось отправить запрос на сервер: " + e.getMessage(), Alert.AlertType.ERROR);
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
