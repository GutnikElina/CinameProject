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
    private int id;                         // Уникальный идентификатор билета
    private int sessionId;                  // Идентификатор сеанса, на который приобретён билет
    private int userId;                     // Идентификатор пользователя, купившего билет
    private String seatNumber;              // Номер места, которое занимает билет
    private LocalDateTime purchaseTime;     // Дата и время покупки билета
    private BigDecimal price;               // Цена билета
}
