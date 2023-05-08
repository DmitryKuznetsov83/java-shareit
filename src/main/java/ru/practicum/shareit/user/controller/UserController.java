package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.EmailLoginAlreadyUsed;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DtoManager;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final ModelMapper modelMapper = new ModelMapper();

	@PostMapping
	public UserDto postUser(@Valid @RequestBody UserDto userDto) {
		if (userDto.getId() != null) {
			throw new IncorrectIdException("User ID must be empty");
		}
		User user = convertUserToEntity(userDto);
		User userSaved = userService.addUser(user);
		return convertUserToDto(userSaved);
	}

	@PatchMapping("/{userId}")
	public UserDto patchUser(@PathVariable Integer userId, @RequestBody @NotNull Map<String, String> body) {
		// checking
		if (body.get("id") != null) {
			throw new IncorrectIdException("User ID in query's body must be empty");
		}
		// get old item
		UserDto userDto = getUserById(userId);

		// patch fields
		UserDto patchedUserDto = DtoManager.patch(userDto, body);

		// validate dto
		DtoManager.validate(patchedUserDto);

		// update
		User patchedUser = userService.updateUser(convertUserToEntity(patchedUserDto));
		return convertUserToDto(patchedUser);
	}

	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable Integer userId) {
		userService.deleteUserById(userId);
	}

	@GetMapping("/{userId}")
	public UserDto getUserById(@PathVariable Integer userId) {
		return convertUserToDto(userService.getUserById(userId));
	}

	@GetMapping
	public List<UserDto> getUsers() {
		return userService
				.getUsers()
				.stream()
				.map(this::convertUserToDto)
				.collect(Collectors.toList());
	}

	@ExceptionHandler({EmailLoginAlreadyUsed.class})
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleEmailLoginAlreadyUsed(final RuntimeException e) {
		return new ErrorResponse(HttpStatus.CONFLICT, "Conflict operation", e.getMessage());
	}


	// PRIVATE
	private UserDto convertUserToDto(User user) {
		return modelMapper.map(user, UserDto.class);
	}

	private User convertUserToEntity(UserDto userDto) {
		return modelMapper.map(userDto, User.class);
	}

}
