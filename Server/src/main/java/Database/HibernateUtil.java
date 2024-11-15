package Database;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            logger.log(Level.SEVERE, "Ошибка создания SessionFactory", ex);
            throw new ExceptionInInitializerError("SessionFactory creation failed: " + ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                logger.info("SessionFactory успешно закрыта.");
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Ошибка закрытия SessionFactory", ex);
            }
        }
    }
}
