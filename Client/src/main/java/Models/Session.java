package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    private int id;                     // Уникальный идентификатор сеанса
    private int movieId;                // Идентификатор фильма, который соответствует этому сеансу
    private int hallId;                 // Идентификатор зала, в котором проходит сеанс
    private LocalDateTime startTime;    // Дата и время начала сеанса
    private LocalDateTime endTime;      // Дата и время окончания сеанса
}
