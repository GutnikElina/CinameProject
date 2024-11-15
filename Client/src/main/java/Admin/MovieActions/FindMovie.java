package Admin.MovieActions;

import Models.Movie;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.HostServices;

import java.io.IOException;

public class FindMovie extends Application {

    private TextField movieIdField;
    private Label titleLabel, genreLabel, durationLabel, releaseDateLabel;
    private TextArea descriptionArea;
    private Button trailerButton;
    private ImageView posterView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Информация о фильме");

        VBox root = new VBox(15);
        root.getStyleClass().add("find-movie-root");
        root.setPadding(new Insets(15));

        HBox inputPanel = createInputPanel();
        GridPane detailsPane = createDetailsPane();
        VBox posterPanel = createPosterPanel();

        trailerButton = new Button("Смотреть трейлер");
        trailerButton.setDisable(true);
        trailerButton.setOnAction(e -> openTrailer());
        trailerButton.getStyleClass().add("find-movie-button");

        HBox mainPane = new HBox(20);
        mainPane.getChildren().addAll(detailsPane, posterPanel);
        mainPane.getStyleClass().add("find-movie-main-pane");

        root.getChildren().addAll(inputPanel, mainPane);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createInputPanel() {
        HBox inputPanel = new HBox(10);
        inputPanel.getStyleClass().add("find-movie-input-panel");
        inputPanel.setAlignment(Pos.CENTER);
        inputPanel.setPadding(new Insets(15));

        movieIdField = UIUtils.createTextField("Введите ID фильма");
        movieIdField.getStyleClass().add("find-movie-text-field");

        Button fetchButton = new Button("Получить данные");
        fetchButton.setOnAction(e -> fetchMovieDetails());
        fetchButton.getStyleClass().add("find-movie-button");

        inputPanel.getChildren().addAll(new Label("ID фильма:"), movieIdField, fetchButton);
        return inputPanel;
    }

    private GridPane createDetailsPane() {
        GridPane detailsPane = new GridPane();
        detailsPane.getStyleClass().add("find-movie-grid-pane");
        detailsPane.setHgap(8);
        detailsPane.setVgap(8);
        detailsPane.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #ddd; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 5, 0, 0, 5);");

        titleLabel = UIUtils.createLabel("", 14);
        genreLabel = UIUtils.createLabel("", 14);
        durationLabel = UIUtils.createLabel("", 14);
        releaseDateLabel = UIUtils.createLabel("", 14);

        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(100);
        descriptionArea.setEditable(false);
        descriptionArea.getStyleClass().add("find-movie-text-area");

        detailsPane.add(new Label("Название:"), 0, 0);
        detailsPane.add(titleLabel, 1, 0);
        detailsPane.add(new Label("Жанр:"), 0, 1);
        detailsPane.add(genreLabel, 1, 1);
        detailsPane.add(new Label("Продолжительность:"), 0, 2);
        detailsPane.add(durationLabel, 1, 2);
        detailsPane.add(new Label("Дата выхода:"), 0, 3);
        detailsPane.add(releaseDateLabel, 1, 3);
        detailsPane.add(new Label("Описание:"), 0, 4);
        detailsPane.add(descriptionArea, 1, 4);
        return detailsPane;
    }

    private VBox createPosterPanel() {
        VBox posterPanel = new VBox(10);
        posterPanel.getStyleClass().add("find-movie-poster-panel");
        posterPanel.setAlignment(Pos.CENTER);
        posterPanel.setPadding(new Insets(6));
        posterView = new ImageView();
        posterView.getStyleClass().add("find-movie-poster");
        posterPanel.getChildren().add(posterView);
        return posterPanel;
    }

    private void fetchMovieDetails() {
        if (!FieldValidator.validatePositiveNumericField(movieIdField, "ID фильма должен быть положительным числом.")) {
            return;
        }

        try {
            int movieId = Integer.parseInt(movieIdField.getText());
            String response = AppUtils.sendToServer("MOVIE;GET;" + movieId);

            handleMovieResponse(response);
        } catch (NumberFormatException e) {
            AppUtils.showAlert("Ошибка", "Ошибка: введите корректный ID фильма!", Alert.AlertType.ERROR);
        }
    }

    private void handleMovieResponse(String response) {
        if (response.startsWith("MOVIE_FOUND;")) {
            String[] movieData = response.split(";");
            if (movieData.length >= 9) {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(movieData[1]));
                movie.setTitle(movieData[2]);
                movie.setGenre(movieData[3]);
                movie.setDuration(Integer.parseInt(movieData[4]));
                movie.setReleaseDate(movieData[5]);
                movie.setPoster(movieData[6]);
                movie.setTrailerUrl(movieData[7]);
                movie.setDescription(movieData[8]);

                updateMovieDetails(movie);
            } else {
                AppUtils.showAlert("Ошибка", "Получены некорректные данные о фильме.", Alert.AlertType.ERROR);
            }
        } else {
            AppUtils.showAlert("Ошибка", "Фильм не найден.", Alert.AlertType.ERROR);
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

        Platform.runLater(() -> {
            String trailerUrl = movie.getTrailerUrl();

            if (trailerUrl != null && !trailerUrl.trim().isEmpty()) {
                trailerButton.setDisable(false);
                trailerButton.setUserData(trailerUrl);
            } else {
                trailerButton.setDisable(true);
            }
        });
    }

    private void openTrailer() {
        try {
            String trailerUrl = (String) trailerButton.getUserData();
            if (trailerUrl != null) {
                HostServices hostServices = getHostServices();
                hostServices.showDocument(trailerUrl);
            }
        } catch (Exception ex) {
            AppUtils.showAlert("Ошибка", "Ошибка при открытии трейлера.", Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }
}