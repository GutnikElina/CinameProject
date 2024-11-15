package Handlers;

import Models.Ticket;
import Services.TicketService;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class TicketHandler implements CommandHandler {
    private final TicketService ticketService;

    @Override
    public void handle(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 5) {
            sendError(out, "Неверный формат запроса");
            return;
        }

        String command = requestParts[1];

        if ("ADD".equals(command)) {
            addTicket(requestParts, out);
        } else {
            sendError(out, "Неизвестная команда!");
        }
    }

    private void addTicket(String[] requestParts, PrintWriter out) {
        try {
            int sessionId = Integer.parseInt(requestParts[2]);
            String seatNumber = requestParts[3];
            int userId = Integer.parseInt(requestParts[4]);
            BigDecimal price = new BigDecimal(requestParts[5]);

            Ticket ticket = new Ticket();
            ticket.setSessionId(sessionId);
            ticket.setSeatNumber(seatNumber);
            ticket.setUserId(userId);
            ticket.setPrice(price);
            ticket.setPurchaseTime(LocalDateTime.now());

            ticketService.add(ticket);
            out.println("SUCCESS: Билет добавлен");
        } catch (Exception e) {
            sendError(out, "Не удалось добавить билет: " + e.getMessage());
        }
    }

    private void sendError(PrintWriter out, String message) {
        out.println("ERROR;" + message);
    }
}
