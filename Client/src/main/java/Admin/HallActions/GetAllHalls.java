package Admin.HallActions;

import Utils.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GetAllHalls {
    @FXML
    private ListView<String> listView;
    @FXML
    private Button backButton;

    private String command = "HALL;GET_ALL;";

    @FXML
    private void initialize() {
        fetchAllHalls();
        backButton.setOnAction(e -> handleBackButton());
    }

    private void fetchAllHalls() {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);

            String response;
            List<String> halls = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.startsWith("END")) {
                    break;
                }
                if (isValidResponse(response)) {
                    halls.add(formatItemResponse(response));
                } else if (response.startsWith("ERROR;")) {
                    handleError("Не удалось получить данные.");
                    break;
                }
            }
            updateListView(halls);

        } catch (IOException e) {
            handleError("Не удалось получить данные.");
            e.printStackTrace();
        }
    }

    private boolean isValidResponse(String response) {
        return response.startsWith("HALL_FOUND;");
    }

    private String formatItemResponse(String response) {
        String[] hallData = response.split(";");
        int hallId = Integer.parseInt(hallData[1]);
        String hallName = hallData[2];
        int capacity = Integer.parseInt(hallData[3]);
        return "ID: " + hallId + " - " + hallName + " (Вместимость: " + capacity + ")";
    }

    private void updateListView(List<String> items) {
        listView.getItems().clear();
        if (items.isEmpty()) {
            listView.getItems().add("Нет доступных залов.");
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
