package Admin.HallActions;

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

public class GetAllHalls extends Application {

    private ListView<String> hallsListView;
    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Все залы");

        hallsListView = new ListView<>();
        hallsListView.setPrefHeight(300);

        backButton = new Button("Назад");
        backButton.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(hallsListView, backButton);

        fetchAllHalls();

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchAllHalls() {
        String message = "HALL;GET_ALL;";

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);

            String response;
            List<String> halls = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_HALLS")) {
                    break;
                }
                if (response.startsWith("HALL_FOUND;")) {
                    String[] hallData = response.split(";");
                    int hallId = Integer.parseInt(hallData[1]);
                    String hallName = hallData[2];
                    int capacity = Integer.parseInt(hallData[3]);
                    halls.add("ID: " + hallId + " - " + hallName + " (Вместимость: " + capacity + ")");
                } else if (response.startsWith("ERROR;")) {
                    AppUtils.showAlert("Ошибка", "Не удалось получить данные о залах.", Alert.AlertType.ERROR);
                    break;
                }
            }
            updateHallsListView(halls);

        } catch (IOException e) {
            AppUtils.showAlert("Ошибка", "Не удалось получить данные о залах.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateHallsListView(List<String> halls) {
        hallsListView.getItems().clear();
        if (halls.isEmpty()) {
            hallsListView.getItems().add("Нет доступных залов.");
        } else {
            hallsListView.getItems().addAll(halls);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
