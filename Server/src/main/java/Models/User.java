package Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;                     // Уникальный идентификатор пользователя

    @Column(nullable = false, unique = true)
    private String username;            // Логин пользователя

    @Column(nullable = false)
    private String password;            // Пароль пользователя

    @Column(nullable = false)
    private String role;                // Роль пользователя

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "guest";
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
