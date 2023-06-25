package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping
	public UserDto postUser(@RequestBody UserDto userDto) {
		return userService.addUser(userDto);
	}

	@PatchMapping("/{userId}")
	public UserDto patchUser(@PathVariable Integer userId, @RequestBody Map<String, String> patch) {
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
