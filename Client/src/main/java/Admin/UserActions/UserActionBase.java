package Admin.UserActions;

import Models.User;
import Utils.AppUtils;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public abstract class UserActionBase extends Application {

    protected void sendUserCommand(User user, Stage stage) {
        String command = String.format("USER;ADD;%s;%s;%s;%s", user.getUsername(), user.getPassword(),
                user.getRole(), user.getCreatedAt());
        sendCommandToServer(command, stage);
    }

    protected void sendUserUpdateCommand(User user, Stage stage) {
        String command = String.format("USER;UPDATE;%d;%s;%s;%s", user.getId(), user.getUsername(), user.getPassword(), user.getRole());
        sendCommandToServer(command, stage);
    }

    protected boolean sendCommandToServer(String command, Stage stage) {
        String response = AppUtils.sendToServer(command);
        if (response.contains("ERROR")) {
            AppUtils.showAlert("Ошибка", "Произошла ошибка при отправке команды на сервер.", Alert.AlertType.ERROR);
            return false;
        } else {
            AppUtils.showAlert("Успех", "Операция выполнена успешно.", Alert.AlertType.INFORMATION);
            if (stage != null) stage.close();
            return true;
        }
    }

    protected User createUser(String username, String password, String role) {
        return new User(0, username, password, role, LocalDateTime.now());
    }
}
