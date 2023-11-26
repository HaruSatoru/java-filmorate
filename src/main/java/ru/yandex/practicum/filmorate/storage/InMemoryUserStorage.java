package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextUserId = 1;

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private void handleInvalidUser(User user) {
        List<String> validationErrors = new ArrayList<>();

        if (user.getEmail().isEmpty()) {
            validationErrors.add("Empty email");
        }
        if (!user.getEmail().contains("@")) {
            validationErrors.add("No '@' in the email field");
        }
        if (user.getLogin().isEmpty()) {
            validationErrors.add("Empty login field");
        }
        if (user.getLogin().contains(" ")) {
            validationErrors.add("Spaces in the login field");
        }
        LocalDate currentDate = LocalDate.now();
        if (user.getBirthday().isAfter(currentDate)) {
            validationErrors.add("Future birthdate");
        }
        if (!validationErrors.isEmpty()) {
            String errorMessage = "Validation error for user data: " + String.join(", ", validationErrors);
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    @Override
    public User addUser(User user) {
        if (isValidUser(user)) {
            if (StringUtils.isBlank(user.getName())) {
                user.setName(user.getLogin());
            }
            user.setId(nextUserId++);
            log.debug("New user added: {}", user);
            users.put(user.getId(), user);
        } else {
            handleInvalidUser(user);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (isValidUser(user) && users.containsKey(user.getId())) {
            if (StringUtils.isBlank(user.getName())) {
                user.setName(user.getLogin());
            }
            log.debug("User data updated: {}", user);
            users.put(user.getId(), user);
        } else {
            if (!users.containsKey(user.getId())) {
                log.debug("Error updating user, invalid user ID: {}", user.getId());
                throw new UserNotFoundException("Error updating user, invalid user ID: " + user.getId());
            }
            handleInvalidUser(user);
        }
        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> {
                    log.warn("User not found with ID=" + userId);
                    return new UserNotFoundException("User not found with ID=" + userId);
                });
    }

    private boolean isValidUser(User user) {
        LocalDate currentDate = LocalDate.now();
        return !user.getEmail().isEmpty()
                && user.getEmail().contains("@")
                && !user.getLogin().isEmpty()
                && !user.getLogin().contains(" ")
                && !user.getBirthday().isAfter(currentDate);
    }

}
