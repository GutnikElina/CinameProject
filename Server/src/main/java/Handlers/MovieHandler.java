package Handlers;

import Models.RequestDTO;
import Models.Movie;
import Services.MovieService;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
public class MovieHandler extends EntityHandler<Movie> {

    private final MovieService movieService;

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        try {
            Movie movie = objectMapper.convertValue(request.getData(), Movie.class);
            movieService.add(movie);
            sendSuccess(out, "MOVIE_ADDED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении фильма: " + e.getMessage());
        }
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        try {
            Movie movie = objectMapper.convertValue(request.getData(), Movie.class);
            movieService.update(movie);
            sendSuccess(out, "MOVIE_UPDATED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении фильма: " + e.getMessage());
        }
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        try {
            int movieId = Integer.parseInt(request.getData().get("id"));
            movieService.delete(movieId);
            sendSuccess(out, "MOVIE_DELETED", null);
        } catch (RuntimeException e) {
            if ("Movie not found".equals(e.getMessage())) {
                sendError(out, "MOVIE_NOT_FOUND");
            } else {
                sendError(out, "Ошибка при удалении фильма: " + e.getMessage());
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении фильма: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        try {
            String title = request.getData().get("title");
            Movie movie = movieService.getByTitle(title);

            if (movie != null) {
                Map<String, Object> movieData = new HashMap<>();
                movieData.put("id", movie.getId());
                movieData.put("title", movie.getTitle());
                movieData.put("genre", movie.getGenre());
                movieData.put("duration", movie.getDuration());
                movieData.put("releaseDate", movie.getReleaseDate());
                movieData.put("description", movie.getDescription());
                sendSuccess(out, "MOVIE_FOUND", movieData);
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
            List<Map<String, Object>> moviesData = new ArrayList<>();

            for (Movie movie : movies) {
                Map<String, Object> movieData = new HashMap<>();
                movieData.put("id", movie.getId());
                movieData.put("title", movie.getTitle());
                movieData.put("genre", movie.getGenre());
                movieData.put("duration", movie.getDuration());
                movieData.put("releaseDate", movie.getReleaseDate());
                movieData.put("description", movie.getDescription());
                moviesData.add(movieData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("movies", moviesData);

            sendSuccess(out, "MOVIES_FOUND", response);
        } catch (Exception e) {
            sendError(out, "Ошибка при получении списка фильмов: " + e.getMessage());
        }
    }
}
