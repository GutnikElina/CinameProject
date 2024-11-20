package Admin.MovieActions;

import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAllMovies {
    @FXML private ListView<String> listView;
    @FXML private Button backButton;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void initialize() {
        fetchAllMovies();
        backButton.setOnAction(e -> handleBackButton());
    }

    private void fetchAllMovies() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            RequestDTO request = new RequestDTO();
            request.setCommand("MOVIE;GET_ALL");
            request.setData(Map.of());

            String jsonRequest = objectMapper.writeValueAsString(request);
            out.println(jsonRequest);

            String response = in.readLine();
            if (response != null) {
                ResponseDTO responseDTO = objectMapper.readValue(response, ResponseDTO.class);

                if ("MOVIES_FOUND".equals(responseDTO.getStatus())) {
                    List<String> movies = new ArrayList<>();
                    List<Map<String, Object>> moviesData = (List<Map<String, Object>>) responseDTO.getData().get("movies");
                    for (Map<String, Object> movieData : moviesData) {
                        int movieId = (Integer) movieData.get("id");
                        String movieTitle = (String) movieData.get("title");
                        String genre = (String) movieData.get("genre");
                        int duration = (Integer) movieData.get("duration");
                        movies.add("ID: " + movieId + " - " + movieTitle + " (Жанр: " + genre + ", Длительность: " + duration + " мин.)");
                    }
                    updateListView(movies);
                } else {
                    handleError(responseDTO.getMessage());
                }
            }

        } catch (IOException e) {
            handleError("Не удалось получить данные.");
            e.printStackTrace();
        }
    }

    private void updateListView(List<String> items) {
        listView.getItems().clear();
        if (items.isEmpty()) {
            listView.getItems().add("Нет доступных фильмов.");
        } else {
            listView.getItems().addAll(items);
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void handleError(String message) {
        AppUtils.showAlert("Ошибка", message, Alert.AlertType.ERROR);
    }
}
