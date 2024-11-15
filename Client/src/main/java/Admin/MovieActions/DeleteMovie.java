package Admin.MovieActions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DeleteMovie extends Application {

    private static final int SERVER_PORT = 12345;
    private TextField idField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Удалить фильм");

        idField = UIUtils.createTextField("ID фильма");

        Button deleteButton = new Button("Удалить фильм");
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(e -> deleteMovieAction(primaryStage));

        VBox vbox = new VBox(15, idField, deleteButton);
        vbox.getStyleClass().add("vbox");
        Scene scene = new Scene(vbox, 350, 220);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deleteMovieAction(Stage primaryStage) {
        if (!FieldValidator.validatePositiveNumericField(idField, "ID фильма должен быть положительным числом.")) {
            return;
        }

        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            int movieId = Integer.parseInt(idField.getText());
            String command = "MOVIE;DELETE;" + movieId;
            out.println(command);

            String response = in.readLine();
            if (response.equals("MOVIE_DELETED")) {
                UIUtils.showAlert("Фильм удален", "Фильм был успешно удален.", Alert.AlertType.INFORMATION);
                primaryStage.close();
            } else if (response.startsWith("ERROR")) {
                UIUtils.showAlert("Ошибка", response.split(";", 2)[1], Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Ошибка при отправке данных на сервер.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}