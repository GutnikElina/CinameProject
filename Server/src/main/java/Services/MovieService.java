package Services;

import Models.Movie;
import org.hibernate.query.Query;
import java.util.List;

public class MovieService extends BaseService implements Repository<Movie> {

    @Override
    public void add(Movie movie) {
        executeTransaction(session -> session.save(movie));
    }

    @Override
    public void update(Movie movie) {
        executeTransaction(session -> session.update(movie));
    }

    @Override
    public List<Movie> getAll() {
        return executeTransactionWithResult(session -> {
            Query<Movie> query = session.createQuery("FROM Movie", Movie.class);
            return query.list();
        });
    }

    @Override
    public Movie getById(int id) {
        return executeTransactionWithResult(session -> session.get(Movie.class, id));
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            Movie movie = session.get(Movie.class, id);
            if (movie != null) {
                session.delete(movie);
            }
        });
    }

    public Movie getByTitle(String title) {
        return executeTransactionWithResult(session -> {
            Query<Movie> query = session.createQuery("FROM Movie WHERE title = :title", Movie.class);
            query.setParameter("title", title);
            return query.uniqueResult();
        });
    }
}
