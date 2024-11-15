package Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;                // Идентификатор фильма

    @Column(nullable = false)
    private String title;          // Название фильма

    @Column(nullable = false)
    private String genre;          // Жанр фильма

    @Column(nullable = false)
    private int duration;          // Продолжительность в минутах

    @Column(name = "release_date", nullable = false)
    private Date releaseDate;      // Дата выхода

    @Column(nullable = false)
    private String description;    // Описание фильма

    private String poster;         // URL постера фильма

    private String trailerUrl;     // URL трейлера фильма
}
