package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int id = 1;
    private static Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        validateFilm(film);
        film.setId(id++);
        films.put(film.getId(), film);
        log.info("Film with id " + film.getId() + " added");
        return film;
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        try {
            if (!films.containsKey(film.getId())) {
                log.error("Film with id {} not found", film.getId());
                throw new ValidationException("Invalid film id: " + film.getId());
            }
            films.put(film.getId(), film);
            log.info("Film with id {} updated", film.getId());
            return ResponseEntity.ok(film);
        } catch (ValidationException ex) {
            log.error("Validation exception: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body((Film) Collections.singletonMap("error", ex.getMessage()));
        }
    }


    private static void validateFilm(Film film) throws ValidationException {
        LocalDate birthDayFilm = LocalDate.of(1895, 12, 28);

        if (film.getReleaseDate().isBefore(birthDayFilm)) {
            log.error("Release date of the film with id {} is before December 28, 1895", film.getId());
            throw new ValidationException("Release date cannot be before December 28, 1895");
        }
    }
}