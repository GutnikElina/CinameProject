package Services;

import Models.Hall;
import Models.Movie;
import org.hibernate.query.Query;
import java.util.List;

public class HallService extends BaseService implements Repository<Hall> {

    @Override
    public void add(Hall hall) {
        executeTransaction(session -> session.save(hall));
    }

    @Override
    public void update(Hall hall) {
        executeTransaction(session -> session.merge(hall));
    }

    @Override
    public List<Hall> getAll() {
        return executeTransactionWithResult(session -> {
            Query<Hall> query = session.createQuery("FROM Hall", Hall.class);
            return query.list();
        });
    }

    @Override
    public Hall getById(int id) {
        return executeTransactionWithResult(session -> session.get(Hall.class, id));
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            Hall hall = session.get(Hall.class, id);
            if (hall != null) {
                session.delete(hall);
            }
        });
    }

    public Hall getByTitle(String name) {
        return executeTransactionWithResult(session -> {
            Query<Hall> query = session.createQuery("FROM Hall WHERE name = :name", Hall.class);
            query.setParameter("name", name);
            return query.uniqueResult();
        });
    }
}
