package Admin.UserActions;

import Utils.AppUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class GetAllUsers extends Application {

    private ListView<String> usersListView;
    private Button backButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Все пользователи");

        usersListView = new ListView<>();
        usersListView.setPrefHeight(300);

        backButton = new Button("Назад");

        backButton.setAlignment(Pos.CENTER);
        backButton.setOnAction(e -> primaryStage.close());

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(usersListView, backButton);

        fetchAllUsers();

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchAllUsers() {
        String message = "USER;GET_ALL;";

        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);

            String response;
            List<String> users = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.equals("END_OF_USERS")) {
                    break;
                }
                if (response.startsWith("USER_FOUND;")) {
                    String[] userData = response.split(";");
                    int userId = Integer.parseInt(userData[1]);
                    String username = userData[2];
                    users.add("ID: " + userId + " - " + username);
                } else if (response.startsWith("ERROR;")) {
                    AppUtils.showAlert("Ошибка", "Не удалось получить данные о пользователях.", Alert.AlertType.ERROR);
                    break;
                }
            }
            updateUsersListView(users);

        } catch (IOException e) {
            AppUtils.showAlert("Ошибка", "Не удалось получить данные о пользователях.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void updateUsersListView(List<String> users) {
        usersListView.getItems().clear();
        if (users.isEmpty()) {
            usersListView.getItems().add("Нет пользователей.");
        } else {
            usersListView.getItems().addAll(users);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
