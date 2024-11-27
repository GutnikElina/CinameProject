package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "halls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;          // Идентификатор зала

    @Column(nullable = false)
    private String name;     // Название зала

    @Column(nullable = false)
    private Integer capacity;    // Вместимость зала
}
