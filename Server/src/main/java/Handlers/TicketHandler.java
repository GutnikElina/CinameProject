package Handlers;

import Models.RequestDTO;
import Models.ResponseDTO;
import Models.Ticket;
import Services.TicketService;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
public class TicketHandler extends EntityHandler<Ticket> {
    private final TicketService ticketService;

    private static final String INSUFFICIENT_DATA = "Недостаточно данных для операции.";

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        if (request.getData() == null || request.getData().size() < 7) {
            sendError(out, INSUFFICIENT_DATA);
            return;
        }

        try {
            Ticket ticket = createTicketFromRequest(request.getData());
            int hallCapacity = ticketService.getHallCapacity(ticket.getSessionId());
            int seatNumber = Integer.parseInt(ticket.getSeatNumber());
            if (seatNumber > hallCapacity) {
                sendError(out, "Место превышает вместимость зала!");
                return;
            }

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
            int ticketId = Integer.parseInt(request.getData().get("ticketId").toString());
            Ticket ticket = ticketService.getById(ticketId);
            if (ticket == null) {
                sendError(out, "Билет с таким ID не найден");
                return;
            }

            // Update ticket with new data
            ticket.setSessionId(Integer.parseInt(request.getData().get("sessionId").toString()));
            ticket.setSeatNumber(request.getData().get("seatNumber").toString());
            ticket.setPrice(new BigDecimal(request.getData().get("price").toString()));
            ticket.setStatus(request.getData().get("status").toString());
            ticket.setRequestType(request.getData().get("requestType").toString());

            ticketService.update(ticket);
            sendSuccess(out, "SUCCESS", Map.of("message", "Билет обновлен"));
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении билета: " + e.getMessage());
        }
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        try {
            int ticketId = Integer.parseInt(request.getData().get("ticketId").toString());
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

            sendSuccess(out, "SUCCESS", Map.of("tickets", tickets));
        } catch (Exception e) {
            sendError(out, "Ошибка при получении списка билетов: " + e.getMessage());
        }
    }

    private Ticket createTicketFromRequest(Map<String, String> data) {
        int sessionId = Integer.parseInt(data.get("sessionId").toString());
        String seatNumber = data.get("seatNumber").toString();
        int userId = Integer.parseInt(data.get("userId").toString());
        BigDecimal price = new BigDecimal(data.get("price").toString());
        String status = data.get("status").toString();
        String requestType = data.get("requestType").toString();

        Ticket ticket = new Ticket();
        ticket.setSessionId(sessionId);
        ticket.setSeatNumber(seatNumber);
        ticket.setUserId(userId);
        ticket.setPrice(price);
        ticket.setPurchaseTime(LocalDateTime.now());
        ticket.setStatus(status);
        ticket.setRequestType(requestType);
        return ticket;
    }

    protected void sendError(PrintWriter out, String message) {
        try {
            ResponseDTO errorResponse = new ResponseDTO("ERROR", message, null);
            out.println(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendSuccess(PrintWriter out, String status, Map<String, Object> data) {
        try {
            ResponseDTO response = new ResponseDTO(status, null, data);
            out.println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            sendError(out, "Ошибка при отправке успешного ответа: " + e.getMessage());
        }
    }
}
