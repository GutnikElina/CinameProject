package Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                // Идентификатор фильма

    @Column(nullable = false)
    private String title;          // Название фильма

    @Column(nullable = false)
    private String genre;          // Жанр фильма

    @Column(nullable = false)
    private Integer duration;          // Продолжительность в минутах

    @Column(name = "release_date", nullable = false)
    private String releaseDate;      // Дата выхода

    @Column(nullable = false)
    private String description;    // Описание фильма

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "title", title,
                "genre", genre,
                "duration", duration,
                "releaseDate", releaseDate,
                "description", description
        );
    }

    public void updateFrom(Movie other) {
        this.title = other.title;
        this.genre = other.genre;
        this.duration = other.duration;
        this.releaseDate = other.releaseDate;
        this.description = other.description;
    }
}
