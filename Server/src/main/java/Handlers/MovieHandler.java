package Handlers;

import Models.Movie;
import Services.MovieService;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
public class MovieHandler extends EntityHandler<Movie> {
    private final MovieService movieService;

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 9) {
            sendError(out, "Недостаточно данных для добавления фильма");
            return;
        }

        try {
            Movie movie = new Movie();
            movie.setTitle(requestParts[2]);
            movie.setGenre(requestParts[3]);
            movie.setDuration(Integer.parseInt(requestParts[4]));
            movie.setReleaseDate(requestParts[5].isEmpty() ? null : java.sql.Date.valueOf(requestParts[5]));
            movie.setPoster(requestParts[6].isEmpty() ? null : requestParts[6]);
            movie.setTrailerUrl(requestParts[7].isEmpty() ? null : requestParts[7]);
            movie.setDescription(requestParts[8].isEmpty() ? null : requestParts[8]);

            movieService.add(movie);
            out.println("MOVIE_ADDED");
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении фильма");
        }
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 9) {
            sendError(out, "Недостаточно данных для обновления фильма");
            return;
        }

        try {
            Movie movie = new Movie();
            movie.setId(Integer.parseInt(requestParts[2]));
            movie.setTitle(requestParts[3]);
            movie.setGenre(requestParts[4]);
            movie.setDuration(Integer.parseInt(requestParts[5]));
            movie.setReleaseDate(requestParts[6].isEmpty() ? null : java.sql.Date.valueOf(requestParts[6]));
            movie.setPoster(requestParts[7].isEmpty() ? null : requestParts[7]);
            movie.setTrailerUrl(requestParts[8].isEmpty() ? null : requestParts[8]);

            movieService.update(movie);
            out.println("MOVIE_UPDATED");
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении фильма");
        }
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Не указан ID фильма");
            return;
        }

        try {
            int movieId = parseInt(requestParts[2], out);
            if (movieId == -1) return;
            movieService.delete(movieId);
            out.println("MOVIE_DELETED");
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении фильма");
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Не указан ID фильма");
            return;
        }

        try {
            int movieId = Integer.parseInt(requestParts[2]);
            Movie movie = movieService.getById(movieId);
            if (movie != null) {
                out.println("MOVIE_FOUND;" + movie.getId() + ";" + movie.getTitle() + ";" +
                        movie.getGenre() + ";" + movie.getDuration() + ";" +
                        movie.getReleaseDate() + ";" + movie.getPoster() + ";" +
                        movie.getTrailerUrl() + ";" + movie.getDescription());
            } else {
                sendError(out, "Фильм не найден");
            }
        } catch (NumberFormatException e) {
            sendError(out, "Неверный формат ID фильма");
        } catch (Exception e) {
            sendError(out, "Ошибка при поиске фильма");
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<Movie> movies = movieService.getAll();
            if (movies.isEmpty()) {
                sendError(out, "Фильмы не найдены");
            } else {
                movies.forEach(movie -> out.println("MOVIE_FOUND;" + movie.getId() + ";" + movie.getTitle() + ";" + movie.getGenre() + ";" + movie.getDuration() + ";" + movie.getReleaseDate() + ";" + movie.getPoster() + ";" + movie.getTrailerUrl() + ";" + movie.getDescription()));
                out.println("END_OF_MOVIES");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении фильмов");
        }
    }
}