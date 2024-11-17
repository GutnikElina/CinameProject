package Admin.TicketActions;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageTickets {

    @FXML
    private Button addTickets;
    @FXML
    private Button deleteTickets;
    @FXML
    private Button findTickets;
    @FXML
    private Button getAllTickets;
    @FXML
    private Button updateTickets;
    @FXML
    private Button out;

    @FXML
    private void initialize() {
        addTickets.setOnAction(e -> openAction("ADD"));
        deleteTickets.setOnAction(e -> openAction("DELETE"));
        findTickets.setOnAction(e -> openAction("GET"));
        getAllTickets.setOnAction(e -> openAction("GET_ALL"));
        updateTickets.setOnAction(e -> openAction("UPDATE"));
        out.setOnAction(e -> closeWindow());
    }

    private void openAction(String action) {
        switch (action) {
            case "ADD":
                new AddTicket().start(new Stage());
                break;
            case "DELETE":
                new DeleteTicket().start(new Stage());
                break;
            case "GET":
                new FindTicket().start(new Stage());
                break;
            case "GET_ALL":
                new GetAllTickets().start(new Stage());
                break;
            case "UPDATE":
                new UpdateTicket().start(new Stage());
                break;
            default:
                UIUtils.showAlert("Ошибка", "Неизвестная команда", Alert.AlertType.ERROR);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) out.getScene().getWindow();
        stage.close();
    }
}

