package Handlers;

import Models.FilmSession;
import Models.RequestDTO;
import Models.ResponseDTO;
import Models.Ticket;
import Services.SessionService;
import Services.TicketService;
import Utils.GsonFactory;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static Utils.ResponseUtil.sendError;
import static Utils.ResponseUtil.sendSuccess;

@AllArgsConstructor
public class TicketHandler extends EntityHandler<Ticket> {
    private final TicketService ticketService;
    private final SessionService sessionService;
    private final Gson gson = GsonFactory.create();
    private static final String INSUFFICIENT_DATA = "Недостаточно данных для операции.";

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        try {
            if (request.getData() == null || request.getData().isEmpty()) {
                sendError(out, INSUFFICIENT_DATA);
                return;
            }

            Ticket ticket = gson.fromJson((String) request.getData().get("data"), Ticket.class);

            if (ticket == null) {
                sendError(out, "Некорректные данные.");
                return;
            }

            int hallCapacity = ticketService.getHallCapacity(ticket.getSessionId());
            int seatNumber = Integer.parseInt(ticket.getSeatNumber());

            if (seatNumber > hallCapacity) {
                sendError(out, "Место превышает вместимость зала!");
                return;
            }

            int sessionId = ticket.getSessionId();
            FilmSession session = sessionService.getById(sessionId);
            ticket.setPrice(session.getPrice());
            ticketService.add(ticket);
            sendSuccess(out, "SUCCESS", Map.of("message", "Билет добавлен"));
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении билета: " + e.getMessage());
        }
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        if (request.getData() == null || request.getData().size() < 5) {
            sendError(out, INSUFFICIENT_DATA);
            return;
        }

        try {
            int ticketId = Integer.parseInt((String) request.getData().get("id"));
            Ticket ticket = ticketService.getById(ticketId);
            if (ticket == null) {
                sendError(out, "Билет с таким ID не найден");
                return;
            }
            ticket.setUserId(Integer.parseInt((String)request.getData().get("userId")));
            ticket.setSessionId(Integer.parseInt((String) request.getData().get("sessionId")));
            ticket.setSeatNumber((String) request.getData().get("seatNumber"));
            ticket.setStatus((String) request.getData().get("status"));
            ticket.setRequestType((String) request.getData().get("requestType"));

            int sessionId = ticket.getSessionId();
            FilmSession session = sessionService.getById(sessionId);
            ticket.setPrice(session.getPrice());

            System.out.println(ticket);
            ticketService.update(ticket);
            sendSuccess(out, "SUCCESS", Map.of("message", "Билет обновлен"));
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении билета: " + e.getMessage());
        }
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        try {
            int ticketId = Integer.parseInt((String) request.getData().get("ticketId"));
            Ticket ticket = ticketService.getById(ticketId);
            if (ticket != null) {
                ticketService.delete(ticketId);
                sendSuccess(out, "SUCCESS", Map.of("message", "Билет удален"));
            } else {
                sendError(out, "Билет с таким ID не найден");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении билета: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        try {
            int ticketId = Integer.parseInt(request.getData().get("ticketId").toString());
            Ticket ticket = ticketService.getById(ticketId);

            if (ticket != null) {
                sendSuccess(out, "SUCCESS", Map.of("ticket", ticket));
            } else {
                sendError(out, "Билет с таким ID не найден");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении данных билета: " + e.getMessage());
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            var tickets = ticketService.getAll();

            if (tickets.isEmpty()) {
                sendSuccess(out, "END_OF_TICKETS", Map.of("tickets", tickets));
                return;
            }

            sendSuccess(out, "SUCCESS", Map.of("tickets", tickets.stream()
                    .map(ticket -> Map.of(
                            "id", String.valueOf(ticket.getId()),
                            "sessionId", String.valueOf(ticket.getSessionId()),
                            "userId", String.valueOf(ticket.getUserId()),
                            "seatNumber", String.valueOf(ticket.getSeatNumber()),
                            "status", ticket.getStatus(),
                            "requestType", ticket.getRequestType(),
                            "price", String.valueOf(ticket.getPrice()),
                            "purchaseTime", ticket.getPurchaseTime().toString()
                    ))
                    .toList()));
        } catch (Exception e) {
            sendError(out, "Ошибка при получении списка билетов: " + e.getMessage());
        }
    }
}
