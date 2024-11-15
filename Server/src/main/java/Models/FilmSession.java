package Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
public class FilmSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;                     // Уникальный идентификатор сеанса

    @Column(name = "movie_id", nullable = false)
    private int movieId;                // Идентификатор фильма, который соответствует этому сеансу

    @Column(name = "hall_id", nullable = false)
    private int hallId;                 // Идентификатор зала, в котором проходит сеанс

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;    // Дата и время начала сеанса

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;      // Дата и время окончания сеанса
}
