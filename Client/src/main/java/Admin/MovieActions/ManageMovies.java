package Admin.MovieActions;

import Utils.AppUtils;
import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageMovies {

    @FXML private Button addFilms;
    @FXML private Button deleteFilms;
    @FXML private Button findFilms;
    @FXML private Button getAllFilms;
    @FXML private Button updateFilms;
    @FXML private Button out;

    @FXML
    private void initialize() {
        addFilms.setOnAction(e -> openAction("ADD"));
        deleteFilms.setOnAction(e -> openAction("DELETE"));
        findFilms.setOnAction(e -> openAction("GET"));
        getAllFilms.setOnAction(e -> openAction("GET_ALL"));
        updateFilms.setOnAction(e -> openAction("UPDATE"));
        out.setOnAction(e -> closeWindow());
    }

    private void openAction(String action) {
        switch (action) {
            case "ADD":
                UIUtils.openNewWindow("/SceneBuilder/AddMovie.fxml", "Добавление фильмов");
                break;
            case "DELETE":
                UIUtils.openNewWindow("/SceneBuilder/DeleteMovie.fxml", "Удаление фильмов");
                break;
            case "GET_ALL":
                UIUtils.openNewWindow("/SceneBuilder/GetAllMovies.fxml", "Просмотр всех фильмов");
                break;
            case "UPDATE":
                UIUtils.openNewWindow("/SceneBuilder/UpdateMovie.fxml", "Обновление фильмов");
                break;
            case "GET":
                UIUtils.openNewWindow("/SceneBuilder/FindMovie.fxml", "Поиск фильмов");
                break;
            default:
                AppUtils.showAlert("Ошибка", "Неизвестная команда для действия с фильмами.", Alert.AlertType.ERROR);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) out.getScene().getWindow();
        stage.close();
    }
}

