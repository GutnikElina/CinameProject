package Admin.MovieActions;

import Models.RequestDTO;
import Models.ResponseDTO;
import Utils.AppUtils;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAllMovies {

    @FXML private ListView<String> listView;
    @FXML private Button backButton;

    private final Gson gson = GsonFactory.create();

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

            String jsonRequest = gson.toJson(request);
            out.println(jsonRequest);

            String response = in.readLine();
            if (response != null) {
                ResponseDTO responseDTO = gson.fromJson(response, ResponseDTO.class);
                if ("MOVIES_FOUND".equals(responseDTO.getStatus())) {
                    Type moviesListType = new TypeToken<List<Map<String, Object>>>() {}.getType();
                    List<Map<String, Object>> moviesData = gson.fromJson(gson.toJson(responseDTO.getData().get("movies")), moviesListType);

                    List<String> movies = new ArrayList<>();
                    for (Map<String, Object> movieData : moviesData) {
                        int movieId = ((Double) movieData.get("id")).intValue();
                        String movieTitle = (String) movieData.get("title");
                        String genre = (String) movieData.get("genre");
                        int duration = ((Double) movieData.get("duration")).intValue();
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
        UIUtils.showAlert("Ошибка", message, Alert.AlertType.ERROR);
    }
}