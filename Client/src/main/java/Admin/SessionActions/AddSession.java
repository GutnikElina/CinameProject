package Admin.SessionActions;

import Admin.GeneralActions.SessionActionBase;
import Models.ResponseDTO;
import Models.Session;
import Utils.AppUtils;
import Utils.FieldValidator;
import Utils.GsonFactory;
import Utils.UIUtils;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSession extends SessionActionBase {

    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField, endTimeField;
    @FXML private ComboBox<String> movieComboBox, hallComboBox;
    @FXML private TextField priceField;
    @FXML private Button addButton, backButton;
    private final Map<String, Integer> movieIdMap = new HashMap<>();
    private final Map<String, Integer> hallIdMap = new HashMap<>();
    private final Gson gson = GsonFactory.create();

    @FXML
    public void initialize() {
        loadMovieAndHallData();
    }

    private void loadMovieAndHallData() {
        try {
            String movieCommand = "{\"command\":\"MOVIE;GET_ALL\"}";
            String movieResponse = AppUtils.sendJsonCommandToServer(movieCommand);
            ResponseDTO responseDTO = gson.fromJson(movieResponse, ResponseDTO.class);

            if ("MOVIES_FOUND".equals(responseDTO.getStatus())) {
                List<Map<String, Object>> movies = (List<Map<String, Object>>) responseDTO.getData().get("movies");
                for (Map<String, Object> movieData : movies) {
                    String movieTitle = (String) movieData.get("title");
                    Integer movieId = ((Double) movieData.get("id")).intValue();
                    movieComboBox.getItems().add(movieTitle);
                    movieIdMap.put(movieTitle, movieId);
                }
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось загрузить фильмы: " + responseDTO.getMessage(), Alert.AlertType.ERROR);
            }

            String hallCommand = "{\"command\":\"HALL;GET_ALL\"}";
            String hallResponse = AppUtils.sendJsonCommandToServer(hallCommand);
            responseDTO = gson.fromJson(hallResponse, ResponseDTO.class);

            if ("HALLS_FOUND".equals(responseDTO.getStatus())) {
                List<Map<String, Object>> halls = (List<Map<String, Object>>) responseDTO.getData().get("halls");
                for (Map<String, Object> hallData : halls) {
                    String hallName = (String) hallData.get("name");
                    Integer hallId = ((Double) hallData.get("id")).intValue();
                    hallComboBox.getItems().add(hallName);
                    hallIdMap.put(hallName, hallId);
                }
            } else {
                UIUtils.showAlert("Ошибка", "Не удалось загрузить залы: " + hallResponse, Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось загрузить данные: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void addSessionAction() {
        boolean valid = true;

        valid &= FieldValidator.validateDatePicker(startDatePicker, "Дата начала не выбрана.");
        valid &= FieldValidator.validateTextField(startTimeField, "Время начала не указано.", 5);
        valid &= FieldValidator.validateTextField(endTimeField, "Время окончания не указано.", 5);
        valid &= FieldValidator.validateComboBox(movieComboBox, "Фильм не выбран.");
        valid &= FieldValidator.validateComboBox(hallComboBox, "Зал не выбран.");
        valid &= validatePriceField(priceField);

        if (!valid) return;

        try {
            LocalDate startDate = startDatePicker.getValue();
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();
            String movieTitle = movieComboBox.getValue();
            String hallName = hallComboBox.getValue();
            Integer movieId = movieIdMap.get(movieTitle);
            Integer hallId = hallIdMap.get(hallName);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startLocalTime = LocalTime.parse(startTime, timeFormatter);
            LocalTime endLocalTime = LocalTime.parse(endTime, timeFormatter);

            LocalDateTime startDateTime = LocalDateTime.of(startDate, startLocalTime);

            LocalDateTime endDateTime = LocalDateTime.of(startDate, endLocalTime);

            if (endDateTime.isBefore(startDateTime)) {
                UIUtils.showAlert("Ошибка", "Время окончания не может быть раньше времени начала.", Alert.AlertType.ERROR);
                startTimeField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
                endTimeField.setStyle("-fx-border-color: #ff4444; -fx-border-width: 2;");
                return;
            }
            Session session = new Session();
            session.setStartTime(startDateTime);
            session.setEndTime(endDateTime);
            session.setMovieId(movieId);
            session.setHallId(hallId);
            session.setPrice(new BigDecimal(priceField.getText()));

            Stage stage = (Stage) addButton.getScene().getWindow();
            sendSessionCommand("SESSION;ADD", session, stage);

        } catch (Exception e) {
            UIUtils.showAlert("Ошибка", "Не удалось добавить сеанс: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validatePriceField(TextField priceField) {
        try {
            String priceText = priceField.getText();
            if (priceText == null || priceText.isEmpty()) {
                UIUtils.showAlert("Ошибка", "Цена должна быть указана!", Alert.AlertType.ERROR);
                return false;
            }
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                UIUtils.showAlert("Ошибка", "Цена должна быть больше нуля!", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            UIUtils.showAlert("Ошибка", "Цена должна быть числом!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}