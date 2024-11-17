package Admin.MovieActions;

import Utils.AppUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GetAllMovies {

    @FXML
    private ListView<String> listView;
    @FXML
    private Button backButton;

    private String command = "MOVIE;GET_ALL;";

    @FXML
    private void initialize() {
        fetchAllMovies();
        backButton.setOnAction(e -> handleBackButton());
    }

    private void fetchAllMovies() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);

            String response;
            List<String> movies = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.startsWith("END")) {
                    break;
                }
                if (isValidResponse(response)) {
                    movies.add(formatItemResponse(response));
                } else if (response.startsWith("ERROR;")) {
                    handleError("Не удалось получить данные.");
                    break;
                }
            }
            updateListView(movies);

        } catch (IOException e) {
            handleError("Не удалось получить данные.");
            e.printStackTrace();
        }
    }

    private boolean isValidResponse(String response) {
        return response.startsWith("MOVIE_FOUND;");
    }

    private String formatItemResponse(String response) {
        String[] movieData = response.split(";");
        int movieId = Integer.parseInt(movieData[1]);
        String movieTitle = movieData[2];
        String genre = movieData[3];
        int duration = Integer.parseInt(movieData[4]);
        return "ID: " + movieId + " - " + movieTitle + " (Жанр: " + genre + ", Длительность: " + duration + " мин.)";
    }

    private void updateListView(List<String> items) {
        listView.getItems().clear();
        if (items.isEmpty()) {
            listView.getItems().add("Нет доступных фильмов.");
        } else {
            listView.getItems().addAll(items);
        }
    }

    private void handleError(String message) {
        AppUtils.showAlert("Ошибка", message, Alert.AlertType.ERROR);
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}

