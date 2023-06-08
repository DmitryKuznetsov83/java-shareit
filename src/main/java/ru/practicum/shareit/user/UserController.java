package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aop.OnCreate;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

	private final UserService userService;

	@PostMapping
	@Validated(OnCreate.class)
	public UserDto postUser(@Valid @RequestBody UserDto userDto) {
		return userService.addUser(userDto);
	}

	@PatchMapping("/{userId}")
	public UserDto patchUser(@PathVariable Integer userId, @RequestBody @NotNull Map<String, String> patch) {
		if (patch.get("id") != null) {
			throw new IncorrectIdException("User ID in query's body must be empty");
		}
		return userService.patchUser(userId, patch);
	}

	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable Integer userId) {
		userService.deleteUserById(userId);
	}

	@GetMapping("/{userId}")
	public UserDto getUserById(@PathVariable Integer userId) {
		return userService.getUserById(userId);
	}

	@GetMapping
	public List<UserDto> getUsers() {
		return userService.getUsers();
	}

}
