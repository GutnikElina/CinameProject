package Handlers;

import Models.Ticket;
import Services.TicketService;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class TicketHandler extends EntityHandler<Ticket> {
    private final TicketService ticketService;

    private static final String INSUFFICIENT_DATA = "Недостаточно данных для операции.";

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 6) {
            sendError(out, INSUFFICIENT_DATA);
            return;
        }

        try {
            Ticket ticket = createTicketFromRequest(requestParts);
            ticketService.add(ticket);
            out.println("SUCCESS;Билет добавлен");
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении билета: " + e.getMessage());
        }
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 6) {
            sendError(out, INSUFFICIENT_DATA);
            return;
        }

        try {
            int ticketId = Integer.parseInt(requestParts[2]);
            Ticket ticket = ticketService.getById(ticketId);
            if (ticket == null) {
                sendError(out, "Билет с таким ID не найден");
                return;
            }

            updateTicketFromRequest(ticket, requestParts);
            ticketService.update(ticket);
            out.println("SUCCESS;Билет обновлен");
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении билета: " + e.getMessage());
        }
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        try {
            int ticketId = parseInt(requestParts[2], out);
            if (ticketId != -1) {
                Ticket ticket = ticketService.getById(ticketId);
                if (ticket != null) {
                    ticketService.delete(ticketId);
                    out.println("SUCCESS: Билет удален");
                } else {
                    out.println("TICKET_NOT_FOUND");
                }
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении билета: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length == 4 && "CHECK".equals(requestParts[1])) {
            checkTicketExists(requestParts, out);
        } else if (requestParts.length == 3) {
            try {
                int ticketId = parseInt(requestParts[2], out);
                if (ticketId != -1) {
                    Ticket ticket = ticketService.getById(ticketId);
                    if (ticket != null) {
                        out.println(formatTicketResponse(ticket));
                    } else {
                        out.println("TICKET_NOT_FOUND");
                    }
                }
            } catch (Exception e) {
                sendError(out, "Ошибка при получении данных билета: " + e.getMessage());
            }
        } else {
            sendError(out, "Некорректный запрос.");
        }
    }

    private void checkTicketExists(String[] requestParts, PrintWriter out) {
        try {
            int sessionId = Integer.parseInt(requestParts[2]);
            String seatNumber = requestParts[3];

            boolean ticketExists = ticketService.existsBySessionAndSeat(sessionId, seatNumber);
            if (ticketExists) {
                out.println("TICKET_EXISTS");
            } else {
                out.println("TICKET_NOT_EXISTS");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при проверке билета: " + e.getMessage());
        }
    }


    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            var tickets = ticketService.getAll();
            if (tickets.isEmpty()) {
                out.println("END_OF_TICKETS");
                return;
            }

            tickets.forEach(ticket -> out.println(formatTicketResponse(ticket)));
            out.println("END_OF_TICKETS");
        } catch (Exception e) {
            sendError(out, "Ошибка при получении списка билетов: " + e.getMessage());
        }
    }

    private Ticket createTicketFromRequest(String[] requestParts) {
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
        return ticket;
    }

    private void updateTicketFromRequest(Ticket ticket, String[] requestParts) {
        ticket.setSessionId(Integer.parseInt(requestParts[3]));
        ticket.setSeatNumber(requestParts[4]);
        ticket.setPrice(new BigDecimal(requestParts[5]));
    }

    private String formatTicketResponse(Ticket ticket) {
        return String.format("TICKET_FOUND;%d;%d;%s;%d;%s;%s",
                ticket.getId(),
                ticket.getSessionId(),
                ticket.getSeatNumber(),
                ticket.getUserId(),
                ticket.getPrice(),
                ticket.getPurchaseTime());
    }
}