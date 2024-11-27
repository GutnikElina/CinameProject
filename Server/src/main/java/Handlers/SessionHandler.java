package Handlers;

import Models.*;
import Services.HallService;
import Services.MovieService;
import Services.SessionService;
import Utils.GsonFactory;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Utils.ResponseUtil.*;

@AllArgsConstructor
public class SessionHandler extends EntityHandler<FilmSession> {
    private final SessionService sessionService;
    private final MovieService movieService;
    private final HallService hallService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        Map<String, Object> requestData = request.getData();
        if (requestData.size() < 5) {
            sendError(out, "Недостаточно данных для добавления сеанса.");
            return;
        }
        String startTime = (String) requestData.get("startTime");
        String endTime = (String) requestData.get("endTime");
        int movieId = Integer.parseInt((String) requestData.get("movieId"));
        int hallId = Integer.parseInt((String) requestData.get("hallId"));
        BigDecimal price = new BigDecimal((String) requestData.get("price"));

        if (startTime == null || endTime == null) {
            sendError(out, "Время начала и окончания не могут быть пустыми.");
            return;
        }

        LocalDateTime start = LocalDateTime.parse(startTime, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endTime, FORMATTER);

        FilmSession filmSession = new FilmSession(movieId, hallId, start, end, price);
        sessionService.add(filmSession);

        sendSuccess(out, "SUCCESS", Map.of("message", "Сеанс добавлен"));
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        Map<String, Object> requestData = request.getData();
        if (requestData.size() < 6) {
            sendError(out, "Недостаточно данных для обновления сеанса.");
            return;
        }

        int sessionId = Integer.parseInt((String) requestData.get("sessionId"));
        FilmSession session = sessionService.getById(sessionId);
        if (session == null) {
            sendError(out, "Сеанс с таким ID не найден.");
            return;
        }
        String startTime = (String) requestData.get("startTime");
        String endTime = (String) requestData.get("endTime");
        BigDecimal price = new BigDecimal((String)requestData.get("price"));

        if (startTime == null || endTime == null) {
            sendError(out, "Время начала и окончания не могут быть пустыми.");
            return;
        }

        LocalDateTime start = LocalDateTime.parse(startTime, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endTime, FORMATTER);

        if (end.isBefore(start)) {
            sendError(out, "Время окончания не может быть раньше времени начала.");
            return;
        }

        System.out.println(session);
        session.setMovieId(Integer.parseInt((String) requestData.get("movieId")));
        session.setHallId(Integer.parseInt((String) requestData.get("hallId")));
        session.setStartTime(start);
        session.setEndTime(end);
        session.setPrice(price);
        System.out.println(session);
        sessionService.update(session);
        sendSuccess(out, "SUCCESS", Map.of("message", "Сеанс обновлен"));
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        int sessionId = Integer.parseInt((String) request.getData().get("sessionId"));

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
        int sessionId = Integer.parseInt((String) request.getData().get("sessionId"));
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
                    "startTime", session.getStartTime().format(FORMATTER),
                    "endTime", session.getEndTime().format(FORMATTER),
                    "price", session.getPrice()
            );

            sendSuccess(out, "SESSION_FOUND", sessionData);
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<FilmSession> sessions = sessionService.getAll();
            if (sessions == null || sessions.isEmpty()) {
                ResponseDTO response = new ResponseDTO("SUCCESS", "END_OF_SESSIONS", Map.of("data", List.of()));
                out.println(gson.toJson(response));
                return;
            }

            List<Map<String, Object>> sessionDataList = new ArrayList<>();
            for (FilmSession session : sessions) {
                String movieTitle = "Неизвестно";
                String hallName = "Неизвестно";

                try {
                    Movie movie = movieService.getById(session.getMovieId());
                    if (movie != null) movieTitle = movie.getTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Hall hall = hallService.getById(session.getHallId());
                    if (hall != null) hallName = hall.getName();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sessionDataList.add(Map.of(
                        "id", session.getId(),
                        "movieTitle", movieTitle,
                        "hallName", hallName,
                        "startTime", session.getStartTime() != null ? session.getStartTime() : "Неизвестно",
                        "endTime", session.getEndTime() != null ? session.getEndTime() : "Неизвестно",
                        "price", session.getPrice()
                ));
            }

            ResponseDTO response = new ResponseDTO("SUCCESS", "SESSION_FOUND", Map.of("data", sessionDataList));
            out.println(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            sendError(out, "Ошибка получения списка сеансов: " + e.getMessage());
        }
    }
}
