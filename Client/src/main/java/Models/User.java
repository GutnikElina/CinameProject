package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;                 // Уникальный идентификатор пользователя
    private String username;            // Логин пользователя
    private String password;            // Пароль пользователя
    private String role;                // Роль пользователя
    private LocalDateTime createdAt;    // Дата и время создания пользователя
    private String token;               // Токен
}
