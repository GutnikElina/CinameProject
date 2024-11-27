package Admin.TicketActions;

import Utils.UIUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageTickets {

    @FXML private Button addTickets;
    @FXML private Button deleteTickets;
    @FXML private Button findTickets;
    @FXML private Button getAllTickets;
    @FXML private Button updateTickets;
    @FXML private Button out;

    @FXML
    private void initialize() {
        addTickets.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/AddTicket.fxml", "Добавление билета"));
        deleteTickets.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/DeleteTicket.fxml", "Удаление билета"));
        findTickets.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/FindTicket.fxml", "Поиск билета"));
        getAllTickets.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/GetAllTickets.fxml", "Просмотр всех билетов"));
        updateTickets.setOnAction(e -> UIUtils.openNewWindow("/SceneBuilder/UpdateTicket.fxml", "Обновление билетов"));
        out.setOnAction(e -> UIUtils.closeCurrentWindow((Stage) out.getScene().getWindow()));
    }
}

