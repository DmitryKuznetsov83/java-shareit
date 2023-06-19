package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserJpaRepository userJpaRepository;

	private UserDto userDto;
	private User user;

	@BeforeEach
	void setUp() {
		userDto = UserDto.builder().name("Name").email("mail@mail.ogr").build();
		user = User.builder().name("Name").email("mail@mail.ogr").build();
	}

	@Test
	void addUser_whenUserIsCorrect_thenUserAdded() {
		// when
		when(userJpaRepository.save(any())).thenReturn(user);
		UserDto addedUser = userService.addUser(userDto);
		// then
		assertThat(addedUser.getName(), equalTo(userDto.getName()));
		assertThat(addedUser.getEmail(), equalTo(userDto.getEmail()));
		verify(userJpaRepository).save(user);
	}

	@Test
	void addUser_whenUserIsNotCorrect_thenUserNotAdded() {
		// when
		doThrow(DataIntegrityViolationException.class).when(userJpaRepository).save(user);
		// then
		assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(userDto));
		verify(userJpaRepository).save(user);
	}

	@Test
	void patchUser_whenCorrectPatch_thenUserIsPatched() {
		// given
		Map<String, String> patch = new HashMap<>();
		patch.put("name", "patched name");
		// when
		when(userJpaRepository.findById(any())).thenReturn(Optional.ofNullable(user));
		when(userJpaRepository.save(any())).then(returnsFirstArg());
		UserDto patchedUser = userService.patchUser(1, patch);
		// then
		assertThat(patchedUser.getName(), equalTo("patched name"));
		assertThat(patchedUser.getEmail(), equalTo("mail@mail.ogr"));
		verify(userJpaRepository).save(any());
	}

	@Test
	void deleteUserById_whenUserFound_thenUserDeleted() {
		// when
		when(userJpaRepository.findById(1)).thenReturn(Optional.ofNullable(user));
		userService.deleteUserById(1);
		// then
		verify(userJpaRepository).deleteById(1);
	}

	@Test
	void deleteUserById_whenUserNotFound_thenUserNotDeleted() {
		// when
		when(userJpaRepository.findById(1)).thenReturn(Optional.empty());
		// then
		assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(1));
		verify(userJpaRepository, never()).deleteById(1);
	}


	@Test
	void getUserById_whenUserFound_thenUserReturned() {
		// when
		when(userJpaRepository.findById(1)).thenReturn(Optional.of(user));
		UserDto userById = userService.getUserById(1);
		// then
		assertThat(userById.getName(), equalTo(userDto.getName()));
		assertThat(userById.getEmail(), equalTo(userDto.getEmail()));
		verify(userJpaRepository).findById(1);
	}

	@Test
	void getUserById_whenUserNotFound_thenExceptionThrown() {
		// when
		doThrow(ResourceNotFoundException.class).when(userJpaRepository).findById(1);
		// then
		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
		verify(userJpaRepository).findById(1);
	}

	@Test
	void getUsers() {
		// when
		userService.getUsers();
		// then
		verify(userJpaRepository).findAll();
	}

}