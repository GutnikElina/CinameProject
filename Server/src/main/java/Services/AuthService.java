package Services;

import Models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public class AuthService extends BaseService {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public boolean register(User user) {
        return executeTransactionWithResult(session -> {
            try {
                Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", user.getUsername());
                User existingUser = query.uniqueResult();
                System.out.println(existingUser);

                if (existingUser != null) {
                    log.warn("Пользователь уже существует: {}", user.getUsername());
                    return Boolean.FALSE;
                }

                user.setRole("guest");
                String token = generateToken(user.getUsername(), user.getRole());
                user.setToken(token);
                user.setCreatedAt(LocalDateTime.now());
                session.save(user);

                return Boolean.TRUE;

            } catch (Exception e) {
                log.error("Ошибка при регистрации пользователя: {}", user.getUsername(), e);
                return Boolean.FALSE;
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
                    log.warn("Неверный логин или пароль для пользователя: {}", username);
                    return null;
                }
            } catch (Exception e) {
                log.error("Ошибка при входе пользователя: {}", username, e);
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
                log.error("Ошибка получения текущего пользователя: {}", e.getMessage(), e);
                return null;
            }
        });
    }

    public String generateToken(String username, String role) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(SECRET_KEY)
                    .compact();
        } catch (Exception e) {
            log.error("Ошибка генерации токена для пользователя: {}", username, e);
            throw e;
        }
    }
}
