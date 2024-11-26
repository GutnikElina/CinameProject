package Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
public class FilmSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                     // Уникальный идентификатор сеанса

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;                // Идентификатор фильма, который соответствует этому сеансу

    @Column(name = "hall_id", nullable = false)
    private Integer hallId;                 // Идентификатор зала, в котором проходит сеанс

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;    // Дата и время начала сеанса

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;      // Дата и время окончания сеанса

    @Column(name = "price", nullable = false)
    private BigDecimal price;               // Цена билета на сеанс

    public FilmSession(int movieId, int hallId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal price) {
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }
}
