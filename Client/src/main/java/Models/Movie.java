package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private int id;                // Идентификатор фильма
    private String title;          // Название фильма
    private String genre;          // Жанр фильма
    private int duration;          // Продолжительность в минутах
    private String releaseDate;    // Дата выхода
    private String description;    // Описание фильма
}

