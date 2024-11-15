package Admin.SessionActions;

import Models.Session;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class UpdateSession extends Application {

    private TextField idField, startTimeField, endTimeField;
    private ComboBox<String> movieComboBox, hallComboBox;
    private Map<String, Integer> movieIdMap = new HashMap<>();
    private Map<String, Integer> hallIdMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Обновить сеанс");

        idField = UIUtils.createTextField("Введите ID сеанса");
        startTimeField = UIUtils.createTextField("Введите время начала сеанса (yyyy-MM-dd HH:mm)");
        endTimeField = UIUtils.createTextField("Введите время окончания сеанса (yyyy-MM-dd HH:mm)");

        movieComboBox = new ComboBox<>();
        hallComboBox = new ComboBox<>();

        loadMovieAndHallData();

        VBox vbox = UIUtils.createVBox(15, Pos.CENTER,
                UIUtils.createLabel("Обновление сеанса", 18),
                idField, movieComboBox, hallComboBox, startTimeField, endTimeField,
                UIUtils.createButton("Обновить сеанс", 150, e -> updateSessionAction(primaryStage), false));

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadMovieAndHallData() {
        String movieCommand = "MOVIE;GET_ALL";
        String movieResponse = AppUtils.sendToServerAndGetFullResponse(movieCommand);
        if (movieResponse.startsWith("MOVIE_FOUND")) {
            String[] movieLines = movieResponse.split("\n");
            for (String line : movieLines) {
                if (line.startsWith("MOVIE_FOUND")) {
                    String[] movieParts = line.split(";");
                    String movieTitle = movieParts[2];
                    Integer movieId = Integer.parseInt(movieParts[1]);
                    movieComboBox.getItems().add(movieTitle);
                    movieIdMap.put(movieTitle, movieId);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить фильмы: " + movieResponse, Alert.AlertType.ERROR);
        }

        String hallCommand = "HALL;GET_ALL";
        String hallResponse = AppUtils.sendToServerAndGetFullResponse(hallCommand);
        if (hallResponse.startsWith("HALL_FOUND")) {
            String[] hallLines = hallResponse.split("\n");
            for (String line : hallLines) {
                if (line.startsWith("HALL_FOUND")) {
                    String[] hallParts = line.split(";");
                    String hallName = hallParts[2];
                    Integer hallId = Integer.parseInt(hallParts[1]);
                    hallComboBox.getItems().add(hallName);
                    hallIdMap.put(hallName, hallId);
                }
            }
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить залы: " + hallResponse, Alert.AlertType.ERROR);
        }
    }

    private void updateSessionAction(Stage stage) {
        boolean valid = FieldValidator.validateNumericField(idField, "Пожалуйста, введите корректный ID сеанса.")
                && FieldValidator.validateTextField(startTimeField, "Время начала сеанса должно быть в формате yyyy-MM-dd HH:mm", 1)
                && FieldValidator.validateTextField(endTimeField, "Время окончания сеанса должно быть в формате yyyy-MM-dd HH:mm", 1);

        if (movieComboBox.getValue() == null || hallComboBox.getValue() == null) {
            UIUtils.showAlert("Ошибка", "Пожалуйста, выберите фильм и зал.", Alert.AlertType.ERROR);
            valid = false;
        }

        if (valid) {
            Session session = createSessionFromInput();
            sendSessionUpdateCommand(session, stage);
        } else {
            UIUtils.showAlert("Ошибка", "Пожалуйста, исправьте ошибки в форме.", Alert.AlertType.ERROR);
        }
    }

    private Session createSessionFromInput() {
        Session session = new Session();
        session.setId(Integer.parseInt(idField.getText()));
        session.setMovieId(movieIdMap.get(movieComboBox.getValue()));
        session.setHallId(hallIdMap.get(hallComboBox.getValue()));
        LocalDateTime startTime = LocalDateTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime endTime = LocalDateTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        return session;
    }


    private void sendSessionUpdateCommand(Session session, Stage stage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedStartTime = session.getStartTime().format(formatter);
        String formattedEndTime = session.getEndTime().format(formatter);

        String command = String.format("SESSION;UPDATE;%d;%d;%d;%s;%s", session.getId(), session.getMovieId(), session.getHallId(),
                formattedStartTime, formattedEndTime);

        String response = AppUtils.sendToServer(command);
        if (response.contains("ERROR")) {
            AppUtils.showAlert("Ошибка", "Произошла ошибка при отправке команды на сервер.", Alert.AlertType.ERROR);
        } else {
            AppUtils.showAlert("Успех", "Сеанс обновлен успешно.", Alert.AlertType.INFORMATION);
            if (stage != null) stage.close();
        }
    }
}