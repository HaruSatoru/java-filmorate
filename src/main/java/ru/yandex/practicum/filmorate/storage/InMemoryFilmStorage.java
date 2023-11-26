package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextFilmId = 1;

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private void handleInvalidFilm(Film film) {
        String errorMessage = "";

        if (film.getName().isBlank()) {
            errorMessage += "Empty name field: " + film.getName();
        }
        if (film.getDescription().length() > 200) {
            errorMessage += "Description length exceeds 200 characters. Actual: " + film.getDescription().length();
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            errorMessage += "Release date before 28.12.1895. Actual: " + film.getReleaseDate();
        }
        if (film.getDuration() < 0) {
            errorMessage += "Negative duration: " + film.getDuration();
        }
        if (!errorMessage.isEmpty()) {
            log.debug("Invalid film. {}", errorMessage);
            throw new ValidationException("Invalid film. " + errorMessage);
        }
    }

    @Override
    public Film addFilm(Film film) {
        if (isValidFilm(film)) {
            film.setId(nextFilmId++);
            log.debug("New film added: {}", film);
            films.put(film.getId(), film);
        } else {
            handleInvalidFilm(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (isValidFilm(film) && films.containsKey(film.getId())) {
            log.debug("Film updated: {}", film);
            films.put(film.getId(), film);
        } else {
            if (!films.containsKey(film.getId())) {
                log.debug("Error updating film. Invalid film ID: {}", film.getId());
                throw new FilmNotFoundException("Error updating film. Invalid film ID: " + film.getId());
            }
            handleInvalidFilm(film);
        }
        return film;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        return Optional.ofNullable(films.get(filmId))
                .orElseThrow(() -> {
                    log.warn("Film not found with ID: {}", filmId);
                    return new FilmNotFoundException("Film not found with ID: " + filmId);
                });
    }

    private boolean isValidFilm(Film film) {
        return !film.getName().isBlank()
                && film.getDescription().length() <= 200
                && film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
                && film.getDuration() > 0;
    }

}