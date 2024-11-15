package Admin.TicketActions;

import Elements.Menu.AdminMenu;
import Utils.UIUtils;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageTickets extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Управление билетами");

        Label titleLabel = UIUtils.createLabel("Управление билетами", 24);
        VBox buttonBox = UIUtils.createVBox(15, Pos.CENTER,
                createActionButton("Добавить билет", "ADD_TICKET"),
                createActionButton("Удалить билет", "DELETE_TICKET"),
                createActionButton("Найти билет", "GET_TICKET"),
                createActionButton("Вывести все билеты", "GET_ALL_TICKETS"),
                createActionButton("Обновить билет", "UPDATE_TICKET")
        );

        Button backButton = UIUtils.createButton("Назад", 300, e -> AdminMenu.show("yourToken"), true);
        VBox vbox = UIUtils.createVBox(20, Pos.CENTER, titleLabel, buttonBox, backButton);

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createActionButton(String text, String action) {
        return UIUtils.createButton(text, 300, e -> openAction(action), false);
    }

    private void openAction(String action) {
        switch (action) {
            case "ADD_TICKET":
                new AddTicket().start(new Stage());
                break;
            case "DELETE_TICKET":
                new DeleteTicket().start(new Stage());
                break;
            case "GET_TICKET":
                new FindTicket().start(new Stage());
                break;
            case "GET_ALL_TICKETS":
                new GetAllTickets().start(new Stage());
                break;
            case "UPDATE_TICKET":
                new UpdateTicket().start(new Stage());
                break;
            default:
                UIUtils.showAlert("Ошибка", "Неизвестная команда", Alert.AlertType.ERROR);
        }
    }
}
