package Handlers;

import Models.FilmSession;
import Models.Hall;
import Models.Movie;
import Services.HallService;
import Services.MovieService;
import Services.SessionService;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
public class SessionHandler extends EntityHandler<FilmSession> {
    private final SessionService sessionService;
    private final MovieService movieService;
    private final HallService hallService;

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(requestParts[2], formatter);
            LocalDateTime endTime = LocalDateTime.parse(requestParts[3], formatter);
            int movieId = Integer.parseInt(requestParts[4]);
            int hallId = Integer.parseInt(requestParts[5]);

            FilmSession filmSession = new FilmSession();
            filmSession.setStartTime(startTime);
            filmSession.setEndTime(endTime);
            filmSession.setMovieId(movieId);
            filmSession.setHallId(hallId);

            sessionService.add(filmSession);
            out.println("SUCCESS: Сеанс добавлен");
        } catch (Exception e) {
            sendError(out, "Не удалось добавить сеанс: " + e.getMessage());
        }
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 7) {
            sendError(out, "Недостаточно данных для обновления сеанса");
            return;
        }
        try {
            int sessionId = Integer.parseInt(requestParts[2]);
            int movieId = Integer.parseInt(requestParts[3]);
            int hallId = Integer.parseInt(requestParts[4]);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startTime = LocalDateTime.parse(requestParts[5], formatter);
            LocalDateTime endTime = LocalDateTime.parse(requestParts[6], formatter);

            if (startTime.isAfter(endTime)) {
                sendError(out, "Время окончания сеанса не может быть раньше времени начала.");
                return;
            }

            FilmSession filmSession = sessionService.getById(sessionId);
            if (filmSession == null) {
                sendError(out, "Сеанс с таким ID не найден");
                return;
            }

            filmSession.setMovieId(movieId);
            filmSession.setHallId(hallId);
            filmSession.setStartTime(startTime);
            filmSession.setEndTime(endTime);

            sessionService.update(filmSession);
            out.println("SUCCESS: Сеанс обновлен");
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении сеанса: " + e.getMessage());
        }
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        try {
            int sessionId = parseInt(requestParts[2], out);
            if (sessionId == -1) return;
            sessionService.delete(sessionId);
            out.println("SUCCESS: Сеанс удален");
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении сеанса: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "ID сеанса не предоставлен");
            return;
        }

        try {
            int sessionId = Integer.parseInt(requestParts[2]);
            FilmSession session = sessionService.getById(sessionId);

            if (session == null) {
                out.println("SESSION_NOT_FOUND");
            } else {
                Movie movie = movieService.getById(session.getMovieId());
                Hall hall = hallService.getById(session.getHallId());

                if (movie == null || hall == null) {
                    sendError(out, "Не удалось получить данные о фильме или зале.");
                    return;
                }

                String movieTitle = movie.getTitle();
                String hallName = hall.getName();
                out.printf("SESSION_FOUND;%d;%s;%s;%s;%s%n",
                        session.getId(),
                        movieTitle,
                        hallName,
                        session.getStartTime(),
                        session.getEndTime());
            }
        } catch (NumberFormatException e) {
            sendError(out, "Неверный формат ID: " + requestParts[2]);
        } catch (Exception e) {
            sendError(out, "Ошибка при обработке запроса: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<FilmSession> sessions = sessionService.getAll();
            if (sessions.isEmpty()) {
                out.println("END_OF_SESSIONS");
                return;
            }

            for (FilmSession session : sessions) {
                Movie movie = movieService.getById(session.getMovieId());
                Hall hall = hallService.getById(session.getHallId());
                String movieTitle = movie.getTitle();
                String hallName = hall.getName();
                out.printf("SESSION_FOUND;%d;%s;%s;%s;%s%n",
                        session.getId(),
                        movieTitle,
                        hallName,
                        session.getStartTime(),
                        session.getEndTime());
            }
            out.println("END_OF_SESSIONS");
        } catch (Exception e) {
            sendError(out, "Ошибка при получении сеансов: " + e.getMessage());
        }
    }

}