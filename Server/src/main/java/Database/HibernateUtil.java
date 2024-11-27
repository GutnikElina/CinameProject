package Database;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Ошибка создания SessionFactory: ", ex);
            throw new ExceptionInInitializerError("SessionFactory creation failed: " + ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                log.info("SessionFactory успешно закрыта.");
            } catch (Exception ex) {
                log.warn("Ошибка закрытия SessionFactory: ", ex);
            }
        }
    }
}
