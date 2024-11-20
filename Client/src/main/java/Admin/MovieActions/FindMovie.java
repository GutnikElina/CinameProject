package Admin.MovieActions;

import Models.Movie;
import Utils.AppUtils;
import Utils.UIUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Models.RequestDTO;
import Models.ResponseDTO;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class FindMovie {
    @FXML private TextField movieTitleField;
    @FXML private Label titleLabel, genreLabel, durationLabel, releaseDateLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Button backButton;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

            String jsonRequest = objectMapper.writeValueAsString(request);

            String response = AppUtils.sendToServer(jsonRequest);

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
            ResponseDTO responseDTO = objectMapper.readValue(response, ResponseDTO.class);
            System.out.println(responseDTO);

            if ("MOVIE_FOUND".equals(responseDTO.getStatus())) {
                Map<String, Object> movieData = responseDTO.getData();
                Movie movie = new Movie();
                movie.setId((Integer) movieData.get("id"));
                movie.setTitle((String) movieData.get("title"));
                movie.setGenre((String) movieData.get("genre"));
                movie.setDuration((Integer) movieData.get("duration"));

                long releaseDateTimestamp = (Long) movieData.get("releaseDate");
                String releaseDate = Instant.ofEpochMilli(releaseDateTimestamp)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("ru"))); // формат с месяцем на русском
                movie.setReleaseDate(releaseDate);

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