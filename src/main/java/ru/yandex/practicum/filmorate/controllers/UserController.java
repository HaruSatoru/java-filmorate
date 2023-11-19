package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int id = 1;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        validateUser(user);

        user.setId(id++);
        users.put(user.getId(), user);
        log.info("User with id {} created", user.getId());
        return user;
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        try {
            if (!users.containsKey(user.getId())) {
                log.error("User with id {} not found", user.getId());
                throw new ValidationException("Invalid user id: " + user.getId());
            }
            users.replace(user.getId(), user);
            log.info("User information with id {} updated", user.getId());
            return ResponseEntity.ok(user);
        } catch (ValidationException ex) {
            log.error("Validation exception: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((User) Collections.singletonMap("error", ex.getMessage()));

        }
    }

    public static void validateUser(User user) throws ValidationException {
        String[] usernameParts = user.getLogin().split(" ");

        if (usernameParts.length > 1) {
            log.error("User with id {} has spaces in the login", user.getId());
            throw new ValidationException("Login should not contain spaces");
        }
    }
}