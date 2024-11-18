package Handlers;

import Models.Movie;
import Services.MovieService;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

@AllArgsConstructor
public class MovieHandler extends EntityHandler<Movie> {
    private final MovieService movieService;

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        Movie movie = parseMovie(requestParts, out);
        if (movie != null) {
            movieService.add(movie);
            out.println("MOVIE_ADDED");
        }
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        Movie movie = parseMovie(requestParts, out);
        if (movie != null) {
            movieService.update(movie);
            out.println("MOVIE_UPDATED");
        }
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Не указан ID фильма");
            return;
        }

        int movieId = parseInt(requestParts[2], out);
        if (movieId == -1) return;

        Movie movie = movieService.getById(movieId);
        if (movie != null) {
            movieService.delete(movieId);
            out.println("MOVIE_DELETED");
        } else {
            sendError(out, "Фильм не найден");
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Не указано название фильма");
            return;
        }

        Movie movie = movieService.getByTitle(requestParts[2]);
        if (movie != null) {
            out.println("MOVIE_FOUND;" + movie.getId() + ";" + movie.getTitle() + ";" + movie.getGenre() + ";" + movie.getDuration() + ";" +
                    movie.getReleaseDate() + ";" + movie.getPoster() + ";" + movie.getTrailerUrl() + ";" + movie.getDescription());
        } else {
            sendError(out, "Фильм не найден");
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        List<Movie> movies = movieService.getAll();
        if (movies.isEmpty()) {
            sendError(out, "Фильмы не найдены");
        } else {
            movies.forEach(movie -> out.println("MOVIE_FOUND;" + movie.getId() + ";" + movie.getTitle() + ";" + movie.getGenre() + ";" +
                    movie.getDuration() + ";" + movie.getReleaseDate() + ";" + movie.getPoster() + ";" + movie.getTrailerUrl() + ";" + movie.getDescription()));
            out.println("END_OF_MOVIES");
        }
    }

    private Movie parseMovie(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 9) {
            sendError(out, "Недостаточно данных для добавления/обновления фильма");
            return null;
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
            movie.setDescription(requestParts.length > 9 && !requestParts[9].isEmpty() ? requestParts[9] : null);
            return movie;
        } catch (Exception e) {
            sendError(out, "Ошибка при обработке данных для фильма");
            return null;
        }
    }
}