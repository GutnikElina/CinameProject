package Elements.Menu;

import Admin.HallActions.ManageHalls;
import Admin.MovieActions.ManageMovies;
import Admin.SessionActions.ManageSessions;
import Admin.TicketActions.ManageTickets;
import Admin.UserActions.ManageUsers;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.UIUtils;
import Utils.AppUtils;

public class AdminMenu {

    private static String token;

    public static void show(String userToken) {
        token = userToken;
        Stage adminStage = new Stage();
        adminStage.setTitle("Меню администратора");

        Label titleLabel = UIUtils.createLabel("МЕНЮ АДМИНИСТРАТОРА", 24);

        Button backButton = UIUtils.createButton("Выйти", 300, e -> {
            adminStage.close();
            MainMenu.show(adminStage);
        }, true);

        VBox buttonBox = UIUtils.createVBox(15, Pos.CENTER,
                createNavigationButton("Управление фильмами", ManageMovies.class),
                createNavigationButton("Управление пользователями", ManageUsers.class),
                createNavigationButton("Управление залами", ManageHalls.class),
                createNavigationButton("Управление сеансами", ManageSessions.class),
                createNavigationButton("Управление билетами", ManageTickets.class)
        );

        VBox vbox = UIUtils.createVBox(20, Pos.CENTER, titleLabel, buttonBox, backButton);
        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        adminStage.setScene(scene);
        adminStage.show();
    }

    private static Button createNavigationButton(String text, Class<? extends Application> appClass) {
        return UIUtils.createButton(text, 300, e -> {
            if (token != null && !token.isEmpty()) {
                try {
                    appClass.getDeclaredConstructor().newInstance().start(new Stage());
                } catch (Exception ex) {
                    AppUtils.showAlert("Ошибка", "Не удалось открыть окно.", Alert.AlertType.ERROR);
                }
            } else {
                AppUtils.showAlert("Ошибка", "Необходим токен для доступа", Alert.AlertType.ERROR);
            }
        }, false);
    }
}