package Handlers;

import Models.RequestDTO;
import Models.FilmSession;
import Models.Hall;
import Models.Movie;
import Services.HallService;
import Services.MovieService;
import Services.SessionService;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class SessionHandler extends EntityHandler<FilmSession> {
    private final SessionService sessionService;
    private final MovieService movieService;
    private final HallService hallService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        Map<String, String> requestData = request.getData();
        if (requestData.size() < 5) {
            sendError(out, "Недостаточно данных для добавления сеанса.");
            return;
        }

        LocalDateTime startTime = parseDateTime(requestData.get("startTime"), out);
        LocalDateTime endTime = parseDateTime(requestData.get("endTime"), out);
        int movieId = Integer.parseInt(requestData.get("movieId"));
        int hallId = Integer.parseInt(requestData.get("hallId"));
        BigDecimal price = new BigDecimal(requestData.get("price"));

        if (endTime.isBefore(startTime)) {
            sendError(out, "Время окончания не может быть раньше времени начала.");
            return;
        }

        FilmSession filmSession = new FilmSession(movieId, hallId, startTime, endTime, price);
        sessionService.add(filmSession);

        sendSuccess(out, "SUCCESS", Map.of("message", "Сеанс добавлен"));
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        Map<String, String> requestData = request.getData();
        if (requestData.size() < 6) {
            sendError(out, "Недостаточно данных для обновления сеанса.");
            return;
        }

        FilmSession session = sessionService.getById(Integer.parseInt(requestData.get("sessionId")));
        if (session == null) {
            sendError(out, "Сеанс с таким ID не найден.");
            return;
        }

        LocalDateTime startTime = parseDateTime(requestData.get("startTime"), out);
        LocalDateTime endTime = parseDateTime(requestData.get("endTime"), out);
        BigDecimal price = new BigDecimal(requestData.get("price"));

        if (startTime == null || endTime == null || endTime.isBefore(startTime)) {
            sendError(out, "Неверные данные для сеанса.");
            return;
        }

        session.setMovieId(Integer.parseInt(requestData.get("movieId")));
        session.setHallId(Integer.parseInt(requestData.get("hallId")));
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setPrice(price);

        sessionService.update(session);

        sendSuccess(out, "SUCCESS", Map.of("message", "Сеанс обновлен"));
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        int sessionId = Integer.parseInt(request.getData().get("sessionId"));

        FilmSession session = sessionService.getById(sessionId);
        if (session != null) {
            sessionService.delete(sessionId);
            sendSuccess(out, "SUCCESS", Map.of("message", "Сеанс удален"));
        } else {
            sendError(out, "SESSION_NOT_FOUND");
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        int sessionId = Integer.parseInt(request.getData().get("sessionId"));
        FilmSession session = sessionService.getById(sessionId);

        if (session == null) {
            sendError(out, "SESSION_NOT_FOUND");
        } else {
            Movie movie = movieService.getById(session.getMovieId());
            Hall hall = hallService.getById(session.getHallId());
            Map<String, Object> sessionData = Map.of(
                    "id", session.getId(),
                    "movieTitle", movie != null ? movie.getTitle() : null,
                    "hallName", hall != null ? hall.getName() : null,
                    "startTime", session.getStartTime(),
                    "endTime", session.getEndTime(),
                    "price", session.getPrice()
            );

            sendSuccess(out, "SESSION_FOUND", sessionData);
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        List<FilmSession> sessions = sessionService.getAll();
        if (sessions.isEmpty()) {
            sendError(out, "END_OF_SESSIONS");
        } else {
            sessions.forEach(session -> {
                Movie movie = movieService.getById(session.getMovieId());
                Hall hall = hallService.getById(session.getHallId());
                Map<String, Object> sessionData = Map.of(
                        "id", session.getId(),
                        "movieTitle", movie != null ? movie.getTitle() : null,
                        "hallName", hall != null ? hall.getName() : null,
                        "startTime", session.getStartTime(),
                        "endTime", session.getEndTime(),
                        "price", session.getPrice()
                );
                sendSuccess(out, "SESSION_FOUND", sessionData);
            });
            sendSuccess(out, "END_OF_SESSIONS", Map.of("message", "Конец списка сеансов"));
        }
    }

    private LocalDateTime parseDateTime(String dateTime, PrintWriter out) {
        try {
            return LocalDateTime.parse(dateTime, FORMATTER);
        } catch (Exception e) {
            sendError(out, "Неверный формат даты/времени: " + dateTime);
            return null;
        }
    }
}