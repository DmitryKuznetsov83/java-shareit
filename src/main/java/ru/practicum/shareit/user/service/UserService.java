package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

	User addUser(User user);

	User updateUser(User user);

	void deleteUserById(int userId);

	User getUserById(int userId);

	List<User> getUsers();

}


