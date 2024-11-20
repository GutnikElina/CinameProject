package Handlers;

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
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class SessionHandler extends EntityHandler<FilmSession> {
    private final SessionService sessionService;
    private final MovieService movieService;
    private final HallService hallService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDateTime parseDateTime(String dateTime, PrintWriter out) {
        try {
            return LocalDateTime.parse(dateTime, FORMATTER);
        } catch (Exception e) {
            sendError(out, "Неверный формат даты/времени: " + dateTime);
            return null;
        }
    }

    private BigDecimal parsePrice(String price, PrintWriter out) {
        try {
            return new BigDecimal(price);
        } catch (Exception e) {
            sendError(out, "Неверный формат цены: " + price);
            return null;
        }
    }

    private void sendSessionInfo(PrintWriter out, FilmSession session) {
        Movie movie = movieService.getById(session.getMovieId());
        Hall hall = hallService.getById(session.getHallId());

        if (movie == null || hall == null) {
            sendError(out, "Не удалось получить данные о фильме или зале.");
            return;
        }

        out.printf("SESSION_FOUND;%d;%s;%s;%s;%s;%s%n",
                session.getId(),
                movie.getTitle(),
                hall.getName(),
                session.getStartTime(),
                session.getEndTime(),
                session.getPrice());
    }

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 7) {
            sendError(out, "Недостаточно данных для добавления сеанса.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(requestParts[2], formatter);
        LocalDateTime endTime = LocalDateTime.parse(requestParts[3], formatter);
        int movieId = Integer.parseInt(requestParts[4]);
        int hallId = Integer.parseInt(requestParts[5]);
        BigDecimal price = BigDecimal.valueOf(Long.parseLong(requestParts[6]));

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("Время окончания не может быть раньше времени начала.");
        }

        System.out.println(Arrays.toString(requestParts));

        FilmSession filmSession = new FilmSession();
        filmSession.setStartTime(startTime);
        filmSession.setEndTime(endTime);
        filmSession.setMovieId(movieId);
        filmSession.setHallId(hallId);
        filmSession.setPrice(price);

        sessionService.add(filmSession);
        out.println("SUCCESS: Сеанс добавлен");
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 8) {
            sendError(out, "Недостаточно данных для обновления сеанса.");
            return;
        }

        FilmSession session = sessionService.getById(Integer.parseInt(requestParts[2]));
        if (session == null) {
            sendError(out, "Сеанс с таким ID не найден.");
            return;
        }

        LocalDateTime startTime = parseDateTime(requestParts[5], out);
        LocalDateTime endTime = parseDateTime(requestParts[6], out);
        BigDecimal price = parsePrice(requestParts[7], out);

        if (startTime == null || endTime == null || price == null || endTime.isBefore(startTime)) {
            sendError(out, "Неверные данные для сеанса.");
            return;
        }

        session.setMovieId(Integer.parseInt(requestParts[3]));
        session.setHallId(Integer.parseInt(requestParts[4]));
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setPrice(price);

        sessionService.update(session);
        out.println("SUCCESS: Сеанс обновлен");
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        int sessionId = parseInt(requestParts[2], out);
        if (sessionId == -1) return;

        if (sessionService.getById(sessionId) != null) {
            sessionService.delete(sessionId);
            out.println("SUCCESS: Сеанс удален");
        } else {
            out.println("SESSION_NOT_FOUND");
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        int sessionId = parseInt(requestParts[2], out);
        if (sessionId == -1) return;

        FilmSession session = sessionService.getById(sessionId);
        if (session == null) {
            out.println("SESSION_NOT_FOUND");
        } else {
            sendSessionInfo(out, session);
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        List<FilmSession> sessions = sessionService.getAll();
        if (sessions.isEmpty()) {
            out.println("END_OF_SESSIONS");
        } else {
            sessions.forEach(session -> sendSessionInfo(out, session));
            out.println("END_OF_SESSIONS");
        }
    }
}