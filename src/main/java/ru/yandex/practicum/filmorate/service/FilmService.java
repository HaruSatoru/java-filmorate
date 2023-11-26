package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    private void updateLike(Integer filmId, Integer userId, boolean addLike) {
        Film film = getFilmById(filmId);
        Collection<User> users = userService.getAllUsers();
        if (!users.contains(userService.getUserById(userId))) {
            log.warn("User not found with ID=" + userId);
            throw new UserNotFoundException("User not found with ID=" + userId);
        }

        if (addLike) {
            film.getLikes().add(userId);
            log.debug("Like added to the film with name=" + film.getName());
        } else {
            film.getLikes().remove(userId);
            log.debug("Like removed from the film with name=" + film.getName());
        }

        updateFilm(film);
    }

    public Film addLike(Integer filmId, Integer userId) {
        updateLike(filmId, userId, true);
        return getFilmById(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        updateLike(filmId, userId, false);
        return getFilmById(filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        log.debug("Request for the list of top films with count=" + count);
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt(film -> ((Film) film).getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer filmId) {
        log.debug("Request for the film with id=" + filmId);
        return filmStorage.getFilmById(filmId);
    }
}
