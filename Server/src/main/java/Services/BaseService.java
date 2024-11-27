package Services;

import Database.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class BaseService {

    protected static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    protected void executeTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Ошибка транзакции: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    protected <R> R executeTransactionWithResult(Function<Session, R> action) {
        Transaction transaction = null;
        R result = null;
        Session session = sessionFactory.getCurrentSession();
        try {
            transaction = session.beginTransaction();
            result = action.apply(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Ошибка транзакции: {}", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
