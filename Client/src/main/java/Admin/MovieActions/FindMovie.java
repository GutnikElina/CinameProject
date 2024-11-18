package Admin.MovieActions;

import Models.Movie;
import Utils.AppUtils;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FindMovie {
    @FXML
    private TextField movieTitleField;
    @FXML
    private Label titleLabel, genreLabel, durationLabel, releaseDateLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ImageView posterView;
    @FXML
    private Button trailerButton;
    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        backButton.setOnAction(e -> handleBackButton());
        trailerButton.setDisable(true);
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

        String response = AppUtils.sendToServer("MOVIE;GET;" + movieTitle);

        if (response == null) {
            UIUtils.showAlert("Ошибка", "Не удалось подключиться к серверу.", Alert.AlertType.ERROR);
            return;
        }

        handleMovieResponse(response);
    }

    private void handleMovieResponse(String response) {
        if (response.startsWith("MOVIE_FOUND;")) {
            String[] movieData = response.split(";");
            if (movieData.length >= 9) {
                Movie movie = new Movie(
                        Integer.parseInt(movieData[1]), movieData[2], movieData[3],
                        Integer.parseInt(movieData[4]), movieData[5], movieData[6],
                        movieData[7], movieData[8]);
                updateMovieDetails(movie);
            } else {
                UIUtils.showAlert("Ошибка", "Получены некорректные данные о фильме.", Alert.AlertType.ERROR);
            }
        } else {
            UIUtils.showAlert("Ошибка", "Фильм не найден.", Alert.AlertType.ERROR);
        }
    }

    private void updateMovieDetails(Movie movie) {
        titleLabel.setText(movie.getTitle());
        genreLabel.setText(movie.getGenre());
        durationLabel.setText(movie.getDuration() + " мин.");
        releaseDateLabel.setText(movie.getReleaseDate());
        descriptionArea.setText(movie.getDescription());

        if (movie.getPoster() != null && !movie.getPoster().isEmpty()) {
            posterView.setImage(new Image(movie.getPoster()));
        } else {
            posterView.setImage(null);
        }

        String trailerUrl = movie.getTrailerUrl();
        trailerButton.setDisable(trailerUrl == null || trailerUrl.trim().isEmpty());
        trailerButton.setUserData(trailerUrl);
    }

    @FXML
    private void openTrailer() {
        try {
            String trailerUrl = (String) trailerButton.getUserData();
            if (trailerUrl != null && !trailerUrl.isEmpty()) {
                AppUtils.openWebPage(trailerUrl);
            } else {
                UIUtils.showAlert("Ошибка", "Трейлер недоступен.", Alert.AlertType.WARNING);
            }
        } catch (Exception ex) {
            UIUtils.showAlert("Ошибка", "Ошибка при открытии трейлера.", Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }
}
