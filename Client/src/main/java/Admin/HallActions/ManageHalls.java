package Admin.HallActions;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.UIUtils;

public class ManageHalls extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Управление залами");

        VBox buttonBox = UIUtils.createVBox(15, Pos.CENTER,
                UIUtils.createButton("Добавить зал", 300, e -> openHallAction("ADD_HALL"), false),
                UIUtils.createButton("Удалить зал", 300, e -> openHallAction("DELETE_HALL"), false),
                UIUtils.createButton("Найти зал", 300, e -> openHallAction("GET_HALL"), false),
                UIUtils.createButton("Вывести все залы", 300, e -> openHallAction("GET_ALL_HALLS"), false),
                UIUtils.createButton("Обновить зал", 300, e -> openHallAction("UPDATE_HALL"), false)
        );

        Button backButton = UIUtils.createButton("Назад", 300, e -> primaryStage.close(), true);
        VBox vbox = UIUtils.createVBox(20, Pos.CENTER, buttonBox, backButton);

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openHallAction(String action) {
        switch (action) {
            case "ADD_HALL":
                new AddHall().start(new Stage());
                break;
            case "DELETE_HALL":
                new DeleteHall().start(new Stage());
                break;
            case "GET_HALL":
                new FindHall().start(new Stage());
                break;
            case "GET_ALL_HALLS":
                new GetAllHalls().start(new Stage());
                break;
            case "UPDATE_HALL":
                new UpdateHall().start(new Stage());
                break;
            default:
                UIUtils.showAlert("Ошибка", "Неизвестная команда", Alert.AlertType.ERROR);
        }
    }
}
