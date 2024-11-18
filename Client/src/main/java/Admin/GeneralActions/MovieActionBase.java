package Admin.GeneralActions;

import Models.Movie;
import Utils.AppUtils;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class MovieActionBase {

    protected void sendMovieCommand(Movie movie, Stage stage) {
        String command = String.format("MOVIE;ADD;%s;%s;%d;%s;%s;%s;%s",
                movie.getTitle(), movie.getGenre(), movie.getDuration(),
                movie.getReleaseDate(), movie.getPoster(), movie.getTrailerUrl(), movie.getDescription());
        String response = AppUtils.sendToServer(command);
        if (response.contains("ERROR")) {
            AppUtils.showAlert("Ошибка", "Произошла ошибка.", Alert.AlertType.ERROR);
        } else {
            AppUtils.showAlert("Успех", "Операция выполнена успешно.", Alert.AlertType.INFORMATION);
            if (stage != null) stage.close();
        }
    }
}
