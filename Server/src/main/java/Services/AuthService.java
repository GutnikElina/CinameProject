package Services;

import Models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.hibernate.query.Query;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

public class AuthService extends BaseService {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public boolean register(User user) {
        return executeTransactionWithResult(session -> {
            Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", user.getUsername());
            User existingUser = query.uniqueResult();

            if (existingUser != null) {
                System.err.println("Пользователь уже существует.");
                return false;
            }

            user.setRole("guest");
            session.save(user);
            return true;
        });
    }

    public String[] login(String username, String password) {
        return executeTransactionWithResult(session -> {
            String queryStr = "FROM User u WHERE u.username = :username AND u.password = :password";
            Query<User> query = session.createQuery(queryStr, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            List<User> users = query.getResultList();

            if (!users.isEmpty()) {
                User user = users.get(0);
                String role = user.getRole();
                String token = generateToken(username, role);
                return new String[]{token, role};
            } else {
                System.err.println("Неверный логин или пароль.");
                return null;
            }
        });
    }

    private String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SECRET_KEY)
                .compact();
    }
}
