package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private Validator validator;
    private FilmController filmController;

    @BeforeEach
    void setup() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void addFilmIfReleaseDateBeforeBirthOfCinema() {
        LocalDate invalidReleaseDate = LocalDate.of(1800, 10, 23);
        Film invalidFilm = new Film(1, "Invalid Film", "Invalid Description", invalidReleaseDate, 100);

        assertThrows(ValidationException.class, () -> filmController.addFilm(invalidFilm));
    }

    @Test
    void updateFilmWithUnknownId() throws ValidationException {
        LocalDate releaseDate = LocalDate.of(1900, 10, 23);
        Film existingFilm = new Film(1, "Existing Film", "Description", releaseDate, 100);
        Film nonExistingFilm = new Film(99, "Non-Existing Film", "Description", releaseDate, 100);

        filmController.addFilm(existingFilm);

        assertThrows(Throwable.class, () -> filmController.updateFilm(nonExistingFilm));
    }

    @Test
    void addFilmWithEmptyName() {
        LocalDate releaseDate = LocalDate.of(1900, 10, 23);
        Film filmWithEmptyName = new Film(1, "", "Description", releaseDate, 100);

        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithEmptyName);

        assertFalse(violations.isEmpty());
    }

    @Test
    void addFilmWithLongDescription() {
        LocalDate releaseDate = LocalDate.of(2000, 5, 5);
        String longDescription = "In the year 180, the death of emperor Marcus Aurelius throws the Roman Empire " +
                "into chaos. Maximus, a skilled general, is betrayed by Commodus, the emperor's corrupt son. " +
                "Maximus escapes execution but is sold into slavery. Now, he seeks revenge, " +
                "fighting as a gladiator in the Colosseum.";

        Film gladiator = new Film(1, "Gladiator", longDescription, releaseDate, 155);

        Set<ConstraintViolation<Film>> violations = validator.validate(gladiator);

        assertFalse(violations.isEmpty());
    }

    @Test
    void addFilmWithNegativeDuration() {
        LocalDate releaseDate = LocalDate.of(1900, 10, 23);
        Film filmWithNegativeDuration = new Film(1, "Negative Duration Film", "Description", releaseDate, -100);

        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithNegativeDuration);

        assertFalse(violations.isEmpty());
    }
}