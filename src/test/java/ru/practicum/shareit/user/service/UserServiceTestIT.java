package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTestIT {

	private final UserService userService;

	@Test
	void getUserById_whenUserPresent_thenReturnUser() {
		// given
		UserDto user = UserDto.builder().name("Name").email("mail@mail.ru").build();
		userService.addUser(user);
		// when
		User userEntityById = userService.getUserEntityById(1);
		// then
		assertThat(userEntityById.getId(), equalTo(1));
	}

	@Test
	void getUserById_whenUserNotPresent_thenThrowResourceNotFoundException() {
		// given
		UserDto user = UserDto.builder().name("Name").email("mail@mail.ru").build();
		userService.addUser(user);

		// when, then
		assertThrows(ResourceNotFoundException.class, () -> userService.getUserEntityById(2));
	}

}
