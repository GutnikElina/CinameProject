package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private Integer id;            // Идентификатор фильма
    private String title;          // Название фильма
    private String genre;          // Жанр фильма
    private Integer duration;      // Продолжительность в минутах
    private String releaseDate;    // Дата выхода
    private String description;    // Описание фильма
}

