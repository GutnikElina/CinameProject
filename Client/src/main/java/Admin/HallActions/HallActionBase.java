package Admin.HallActions;

import Models.Hall;
import Utils.AppUtils;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class HallActionBase extends Application {

    protected void sendHallCommand(String action, Hall hall, Stage stage) {
        String command = String.format("HALL;%s;%d;%s;%d", action, hall.getId(), hall.getName(), hall.getCapacity());
        String response = AppUtils.sendToServer(command);
        if (response.contains("ERROR")) {
            AppUtils.showAlert("Ошибка", "Произошла ошибка.", Alert.AlertType.ERROR);
        } else {
            AppUtils.showAlert("Успех", "Операция выполнена успешно.", Alert.AlertType.INFORMATION);
            if (stage != null) stage.close();
        }
    }
}
