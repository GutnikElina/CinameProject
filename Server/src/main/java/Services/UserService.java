package Services;

import Models.User;
import org.hibernate.query.Query;
import java.util.List;

public class UserService extends BaseService implements Repository<User> {

    @Override
    public void add(User user) {
        executeTransaction(session -> session.save(user));
    }

    @Override
    public void update(User user) {
        executeTransaction(session -> {
            User existingUser = session.get(User.class, user.getId());
            if (existingUser != null) {
                session.merge(user);
            } else {
                throw new IllegalArgumentException("Пользователь с таким ID не существует");
            }
        });
    }

    @Override
    public List<User> getAll() {
        return executeTransactionWithResult(session -> {
            Query<User> query = session.createQuery("FROM User", User.class);
            return query.list();
        });
    }

    @Override
    public User getById(int id) {
        return executeTransactionWithResult(session -> session.get(User.class, id));
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
            }
        });
    }
}
