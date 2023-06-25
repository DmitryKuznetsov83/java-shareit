package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aop.OnCreate;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.user.dto.UserDto;


import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

	private final UserClient userClient;

	@PostMapping
	@Validated(OnCreate.class)
	public ResponseEntity<Object> postUser(@RequestBody @Validated(OnCreate.class)  UserDto userDto) {
		log.info("Create user {}", userDto);
		return userClient.postUser(userDto);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<Object> patchUser(@PathVariable Integer userId, @RequestBody UserDto userDto) {
		if (userDto.getId() != null) {
			throw new IncorrectIdException("User ID in query's body must be empty");
		}
		log.info("Patch user with id {}", userId);
		return userClient.patchUser(userId, userDto);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Object> deleteUser(@PathVariable @Positive Integer userId) {
		log.info("Delete user with id {}", userId);
		return userClient.deleteUser(userId);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<Object> getUserById(@PathVariable Integer userId) {
		log.info("Get user by id {}", userId);
		return userClient.getUserById(userId);
	}

	@GetMapping
	public ResponseEntity<Object> getUsers() {
		log.info("Get users");
		return userClient.getUsers();
	}

}
