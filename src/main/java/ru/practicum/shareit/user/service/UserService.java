package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {

	UserDto addUser(UserDto user);

	UserDto patchUser(Integer userId, Map<String, String> patch);

	void deleteUserById(int userId);

	UserDto getUserById(int userId);

	User getUserEntityById(int userId);

	List<UserDto> getUsers();

}


