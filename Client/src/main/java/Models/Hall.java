package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hall {
    private int id;          // Идентификатор зала
    private String name;     // Название зала
    private int capacity;    // Вместимость зала
}
