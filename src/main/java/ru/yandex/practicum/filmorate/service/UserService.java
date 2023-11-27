package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers());
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    private void updateFriendship(Integer userId, Integer friendId, boolean addFriendship) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (addFriendship) {
            user.getFriendIds().add(friendId);
            friend.getFriendIds().add(userId);
            log.debug("User with id =" + userId + " added friend with id =" + friendId);
        } else {
            user.getFriendIds().remove(friendId);
            friend.getFriendIds().remove(userId);
            log.debug("Friend with id =" + friendId + " removed from user with id =" + userId);
        }
        updateUser(user);
        updateUser(friend);
    }

    public User addFriendship(Integer userId, Integer friendId) {
        updateFriendship(userId, friendId, true);
        return getUserById(userId);
    }

    public User deleteFriendship(Integer userId, Integer friendId) {
        updateFriendship(userId, friendId, false);
        return getUserById(userId);
    }

    public List<User> getUserFriends(Integer userId) {
        Set<Integer> friendsIds = getUserById(userId).getFriendIds();
        return getAllUsers().stream()
                .filter(user -> friendsIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> userIds = getUserById(userId).getFriendIds();
        Set<Integer> otherIds = getUserById(otherId).getFriendIds();
        return getAllUsers().stream()
                .filter(user -> userIds.contains(user.getId()) && otherIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public User getUserById(Integer userId) {
        log.debug("Request to get user with id=" + userId);
        return userStorage.getUserById(userId);
    }
}
