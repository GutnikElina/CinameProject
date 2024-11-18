package Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;                         // Уникальный идентификатор билета

    @Column(name = "session_id", nullable = false)
    private int sessionId;                  // Идентификатор сеанса, на который приобретён билет

    @Column(name = "user_id", nullable = false)
    private int userId;                     // Идентификатор пользователя, купившего билет

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;              // Номер места, которое занимает билет

    @Column(nullable = false)
    private BigDecimal price;               // Цена билета

    @Column(name = "status", nullable = false)
    private String status;                  // Новый статус билета ("PENDING", "CONFIRMED", "CANCELLED", "EXCHANGED")

    @Column(name = "requestType", nullable = false)
    private String requestType;             // Тип запроса ("PURCHASE", "CANCEL", "EXCHANGE")

    @Column(name = "purchase_time", nullable = false)
    private LocalDateTime purchaseTime;     // Дата и время покупки билета

}
