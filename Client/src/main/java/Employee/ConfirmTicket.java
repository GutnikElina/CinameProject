package Employee;

import Models.Ticket;
import Utils.AppUtils;
import Utils.UIUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfirmTicket {
    @FXML private ListView<HBox> ticketsListView;
    @FXML private Button backButton;

    @FXML
    private void initialize() {
        loadPendingTickets();
    }

    private void loadPendingTickets() {
        String response = AppUtils.sendToServerAndGetFullResponse("TICKET;GET_ALL;");

        if (response.startsWith("TICKET_FOUND")) {
            List<Ticket> tickets = parseTickets(response);

            List<HBox> ticketItems = tickets.stream()
                    .map(ticket -> {
                        Label ticketLabel = new Label(String.format("ID: %d, Место: %s, Статус: %s",
                                ticket.getId(), ticket.getSeatNumber(), ticket.getStatus()));
                        ticketLabel.setFont(new Font("Segoe UI", 14));

                        Button confirmButton = new Button("Подтвердить");
                        confirmButton.setDisable(!ticket.getStatus().equals("PENDING"));
                        confirmButton.setOnAction(e -> confirmTicket(ticket));

                        return new HBox(10, ticketLabel, confirmButton);
                    })
                    .collect(Collectors.toList());

            ticketsListView.setItems(FXCollections.observableArrayList(ticketItems));
        } else if (response.contains("END_OF_TICKETS")) {
            System.out.println("Билеты не найдены.");
        } else {
            System.out.println("Получены не верные данные: " + response);
        }
    }

    private List<Ticket> parseTickets(String response) {
        String[] lines = response.split("\n");

        return Arrays.stream(lines)
                .filter(line -> line.startsWith("TICKET_FOUND"))
                .map(line -> {
                    String[] parts = line.split(";");
                    if (parts.length < 9) {
                        System.out.println("Invalid ticket format, skipping: " + line);
                        return null;
                    }

                    Ticket ticket = new Ticket();
                    try {
                        ticket.setId(Integer.parseInt(parts[1]));
                        ticket.setSessionId(Integer.parseInt(parts[2]));
                        ticket.setSeatNumber(parts[3]);
                        ticket.setUserId(Integer.parseInt(parts[4]));
                        ticket.setPrice(BigDecimal.valueOf(Double.parseDouble(parts[5])));
                        ticket.setStatus(parts[6]);
                        ticket.setRequestType(parts[7]);
                        ticket.setPurchaseTime(parseDate(parts[8]));
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        return null;
                    }
                    return ticket;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void confirmTicket(Ticket ticket) {
        String confirmResponse = AppUtils.sendToServer(String.format("TICKET;UPDATE;CONFIRM;%d", ticket.getId()));
        System.out.println(confirmResponse);
        if (confirmResponse.startsWith("SUCCESS")) {
            UIUtils.showAlert("Успех", "Билет подтвержден", Alert.AlertType.INFORMATION);
            loadPendingTickets();
        } else {
            UIUtils.showAlert("Ошибка", "Не удалось подтвердить билет", Alert.AlertType.ERROR);
        }
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге времени: " + dateStr);
            return null;
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRefreshButton() {
        loadPendingTickets();
    }
}
