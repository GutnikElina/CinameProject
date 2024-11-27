package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hall {
    private Integer id;          // Идентификатор зала
    private String name;         // Название зала
    private Integer capacity;    // Вместимость зала
}
