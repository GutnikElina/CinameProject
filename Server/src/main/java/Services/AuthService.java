package Services;

import Models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.hibernate.query.Query;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService extends BaseService {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public boolean register(User user) {
        return executeTransactionWithResult(session -> {
            try {
                Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", user.getUsername());
                User existingUser = query.uniqueResult();

                if (existingUser != null) {
                    logger.warning("Пользователь уже существует: " + user.getUsername());
                    return false;
                }

                user.setRole("guest");
                String token = generateToken(user.getUsername(), user.getRole());
                user.setToken(token);
                session.save(user);
                return true;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка при регистрации пользователя: " + user.getUsername(), e);
                throw e;
            }
        });
    }

    public String[] login(String username, String password) {
        return executeTransactionWithResult(session -> {
            try {
                String queryStr = "FROM User u WHERE u.username = :username AND u.password = :password";
                Query<User> query = session.createQuery(queryStr, User.class);
                query.setParameter("username", username);
                query.setParameter("password", password);
                List<User> users = query.getResultList();

                if (!users.isEmpty()) {
                    User user = users.get(0);
                    String role = user.getRole();
                    String token = generateToken(username, role);

                    user.setToken(token);
                    session.update(user);
                    return new String[]{token, role};
                } else {
                    logger.warning("Неверный логин или пароль для пользователя: " + username);
                    return null;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка при входе пользователя: " + username, e);
                throw e;
            }
        });
    }

    public User getCurrentUser(String token) {
        return executeTransactionWithResult(session -> {
            try {
                var claims = Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();

                Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", username);
                return query.uniqueResult();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка получения текущего пользователя: " + e.getMessage(), e);
                return null;
            }
        });
    }

    private String generateToken(String username, String role) {
        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                    .signWith(SECRET_KEY)
                    .compact();
            System.out.println("Generated token: " + token);
            return token;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка генерации токена для пользователя: " + username, e);
            throw e;
        }
    }

    public boolean hasPermission(String token, String requiredRole) {
        System.out.println("Token for validation: " + token);
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String role = claims.get("role", String.class);
            return role != null && (role.equals(requiredRole) || role.equals("admin"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка проверки токена: " + e.getMessage(), e);
            return false;
        }
    }
}
