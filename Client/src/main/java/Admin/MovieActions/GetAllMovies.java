package Admin.MovieActions;

import Utils.AppUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GetAllMovies extends Application {

    private ListView<String> moviesListView;
    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Все фильмы");

        moviesListView = new ListView<>();
        moviesListView.setPrefHeight(300);

        backButton = new Button("Назад");
        backButton.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(moviesListView, backButton);

        fetchAllMovies();

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchAllMovies() {
        String message = "MOVIE;GET_ALL;";

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);

            String response;
            List<String> movies = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_MOVIES")) {
                    break;
                }
                if (response.startsWith("MOVIE_FOUND;")) {
                    String[] movieData = response.split(";");
                    int movieId = Integer.parseInt(movieData[1]);
                    String movieTitle = movieData[2];
                    String genre = movieData[3];
                    int duration = Integer.parseInt(movieData[4]);
                    String releaseDate = movieData[5];
                    String poster = movieData[6];
                    String trailerUrl = movieData[7];
                    String description = movieData[8];
                    movies.add("ID: " + movieId + " - " + movieTitle + " (Жанр: " + genre + ", Длительность: " + duration + " мин.)");
                } else if (response.startsWith("ERROR;")) {
                    AppUtils.showAlert("Ошибка", "Не удалось получить данные о фильмах.", Alert.AlertType.ERROR);
                    break;
                }
            }
            updateMoviesListView(movies);

        } catch (IOException e) {
            AppUtils.showAlert("Ошибка", "Не удалось получить данные о фильмах.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateMoviesListView(List<String> movies) {
        moviesListView.getItems().clear();
        if (movies.isEmpty()) {
            moviesListView.getItems().add("Нет доступных фильмов.");
        } else {
            moviesListView.getItems().addAll(movies);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}