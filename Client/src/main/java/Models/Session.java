package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    private Integer id;                 // Уникальный идентификатор сеанса
    private Integer movieId;            // Идентификатор фильма, который соответствует этому сеансу
    private Integer hallId;             // Идентификатор зала, в котором проходит сеанс
    private LocalDateTime startTime;    // Дата и время начала сеанса
    private LocalDateTime endTime;      // Дата и время окончания сеанса
    private BigDecimal price;           // Цена билета на сеанс
}
