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
        addFilms.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/AddMovie.fxml", "Добавление фильмов"));
        deleteFilms.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/DeleteMovie.fxml", "Удаление фильмов"));
        findFilms.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/FindMovie.fxml", "Поиск фильмов"));
        getAllFilms.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/GetAllMovies.fxml", "Просмотр всех фильмов"));
        updateFilms.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/UpdateMovie.fxml", "Обновление фильмов"));
        out.setOnAction(e -> UIUtils.closeCurrentWindow((Stage) out.getScene().getWindow()));
    }
}

