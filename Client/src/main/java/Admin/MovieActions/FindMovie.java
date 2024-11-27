package Admin.MovieActions;

import Models.Movie;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Models.RequestDTO;
import Models.ResponseDTO;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class FindMovie {

    @FXML private TextField movieTitleField;
    @FXML private Label titleLabel, genreLabel, durationLabel, releaseDateLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button backButton;
    private final Gson gson = GsonFactory.create();

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> handleBackButton());
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void fetchMovieDetails() {
        String movieTitle = movieTitleField.getText().trim();

        if (movieTitle.isEmpty()) {
            UIUtils.showAlert("Ошибка", "Название фильма не может быть пустым.", Alert.AlertType.WARNING);
            return;
        }
        sendMovieFindRequest(movieTitle);
    }

    private void sendMovieFindRequest(String movieTitle) {
        try {
            RequestDTO request = new RequestDTO();
            request.setCommand("MOVIE;GET");
            request.setData(Map.of("title", movieTitle));

            String jsonRequest = gson.toJson(request);
            String response = AppUtils.sendJsonCommandToServer(jsonRequest);

            if (response == null) {
                UIUtils.showAlert("Ошибка", "Не удалось подключиться к серверу.", Alert.AlertType.ERROR);
                return;
            }
            handleMovieResponse(response);
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Произошла ошибка при отправке запроса.", Alert.AlertType.ERROR);
        }
    }

    private void handleMovieResponse(String response) {
        try {
            ResponseDTO responseDTO = gson.fromJson(response, ResponseDTO.class);
            if ("MOVIE_FOUND".equals(responseDTO.getStatus())) {
                Type movieDataType = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> responseData = gson.fromJson(gson.toJson(responseDTO.getData()), movieDataType);

                // Извлекаем объект "movie" из ответа
                Map<String, Object> movieData = (Map<String, Object>) responseData.get("movie");

                if (movieData == null) {
                    UIUtils.showAlert("Ошибка", "Данные фильма отсутствуют.", Alert.AlertType.ERROR);
                    return;
                }

                Movie movie = new Movie();

                movie.setId((movieData.get("id") instanceof Double)
                        ? ((Double) movieData.get("id")).intValue()
                        : (Integer) movieData.get("id"));
                movie.setTitle((String) movieData.get("title"));
                movie.setGenre((String) movieData.get("genre"));
                movie.setDuration((movieData.get("duration") instanceof Double)
                        ? ((Double) movieData.get("duration")).intValue()
                        : (Integer) movieData.get("duration"));
                String releaseDate = (String) movieData.get("releaseDate");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(releaseDate, formatter);
                movie.setReleaseDate(date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("ru"))));

                movie.setDescription((String) movieData.get("description"));
                updateMovieDetails(movie);
            } else {
                UIUtils.showAlert("Ошибка", responseDTO.getMessage(), Alert.AlertType.INFORMATION);
            }

        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Произошла ошибка при обработке ответа от сервера.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateMovieDetails(Movie movie) {
        titleLabel.setText(movie.getTitle());
        genreLabel.setText(movie.getGenre());
        durationLabel.setText(movie.getDuration() + " мин.");
        releaseDateLabel.setText(movie.getReleaseDate());
        descriptionArea.setText(movie.getDescription());
    }
}