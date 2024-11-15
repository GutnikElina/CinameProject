package Services;

import Models.FilmSession;
import org.hibernate.query.Query;
import java.util.List;

public class SessionService extends BaseService implements Repository<FilmSession> {

    @Override
    public void add(FilmSession filmSession) {
        executeTransaction(session -> session.save(filmSession));
    }

    @Override
    public void update(FilmSession filmSession) {
        executeTransaction(session -> session.update(filmSession));
    }

    @Override
    public List<FilmSession> getAll() {
        return executeTransactionWithResult(session -> {
            Query<FilmSession> query = session.createQuery("FROM FilmSession", FilmSession.class);
            return query.list();
        });
    }

    @Override
    public FilmSession getById(int id) {
        return executeTransactionWithResult(session -> session.get(FilmSession.class, id));
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            FilmSession filmSession = session.get(FilmSession.class, id);
            if (filmSession != null) {
                session.delete(filmSession);
            }
        });
    }
}
