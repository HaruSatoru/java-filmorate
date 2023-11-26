package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.debug("User with id =" + userId + " added friend with id =" + friendId);
        } else {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
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
        Set<Integer> friendsIds = getUserById(userId).getFriends();
        return getAllUsers().stream()
                .filter(user -> friendsIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> userIds = getUserById(userId).getFriends();
        Set<Integer> otherIds = getUserById(otherId).getFriends();
        return getAllUsers().stream()
                .filter(user -> userIds.contains(user.getId()) && otherIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    public User getUserById(Integer userId) {
        log.debug("Request to get user with id=" + userId);
        return userStorage.getUserById(userId);
    }
}
