package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

	// USERS - CRUD
	User addUser(User user);

	User updateUser(User user);

	User getUser(Integer id);

	List<User> getUsers();

	void deleteUserById(Integer userId);

	// USERS - Checking
	boolean emailAlreadyUsed(String email);

	boolean emailAlreadyUsed(String email, Integer excludedUserId);

	boolean loginAlreadyUsed(String login);

	boolean loginAlreadyUsed(String login, Integer excludedUserId);

}
