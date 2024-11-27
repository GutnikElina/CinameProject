package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Integer id;                     // Уникальный идентификатор билета
    private Integer sessionId;              // Идентификатор сеанса, на который приобретён билет
    private Integer userId;                 // Идентификатор пользователя, купившего билет
    private String seatNumber;              // Номер места, которое занимает билет
    private String status;                  // Новый статус билета ("PENDING", "CONFIRMED", "CANCELLED", "EXCHANGED")
    private String requestType;             // Тип запроса ("PURCHASE", "CANCEL", "EXCHANGE")
    private LocalDateTime purchaseTime;     // Дата и время покупки билета
}
