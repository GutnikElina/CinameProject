package Handlers;

import Models.RequestDTO;
import Models.Movie;
import Models.ResponseDTO;
import Services.MovieService;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import Utils.ResponseUtil;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static Utils.ResponseUtil.*;

@AllArgsConstructor
public class MovieHandler extends EntityHandler<Movie> {

    private final MovieService movieService;

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        try {
            Movie movie = gson.fromJson(gson.toJson(request.getData()), Movie.class);
            movieService.add(movie);
            sendSuccess(out, "MOVIE_ADDED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении фильма: " + e.getMessage());
        }
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        try {
            Movie movie = gson.fromJson(gson.toJson(request.getData()), Movie.class);
            Movie existingMovie = movieService.getById(movie.getId());

            if (existingMovie == null) {
                sendError(out, "Фильм с таким ID не найден.");
                return;
            }

            existingMovie.updateFrom(movie);
            movieService.update(existingMovie);
            sendSuccess(out, "MOVIE_UPDATED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении фильма: " + e.getMessage());
        }
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        try {
            int movieId = ((Double) request.getData().get("id")).intValue();
            movieService.delete(movieId);
            sendSuccess(out, "MOVIE_DELETED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении фильма: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        try {
            String title = (String) request.getData().get("title");
            Movie movie = movieService.getByTitle(title);

            if (movie != null) {
                sendSuccess(out, "MOVIE_FOUND", Map.of("movie", movie));
            } else {
                sendError(out, "Фильм не найден");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при поиске фильма: " + e.getMessage());
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<Movie> movies = movieService.getAll();
            sendSuccess(out, "MOVIES_FOUND", Map.of(
                    "movies", movies.stream().map(Movie::toMap).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            sendError(out, "Ошибка при получении списка фильмов: " + e.getMessage());
        }
    }
}