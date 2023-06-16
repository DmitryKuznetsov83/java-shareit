package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

	@MockBean
	UserService userService;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Test
	@SneakyThrows
	void postUser_whenCorrectUser_thenReturnUser() {
		// given
		UserDto requestDto = UserDto.builder()
				.name("User A")
				.email("user_a@mail.org")
				.build();

		// when
		when(userService.addUser(any())).thenReturn(requestDto);

		mvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(requestDto)))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo(requestDto.getName())),
						jsonPath("$.email", equalTo(requestDto.getEmail()))
				);

	}

	@Test
	@SneakyThrows
	void postUser_whenBadEmail_thenBadRequest() {
		// given
		UserDto requestDto = UserDto.builder()
				.name("User A")
				.email("bad_mail.org")
				.build();

		// when
		mvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(requestDto)))

				// then
				.andExpect(status().isBadRequest());

	}

	@Test
	@SneakyThrows
	void patchUser_whenPatchNotHasId() {
		// given
		Map<String, String> patch = new HashMap<>();
		patch.put("name", "updated User A");
		UserDto patchedUserDto = UserDto.builder().name("updated User A").email("user_a@mail.org").build();

		// when
		when(userService.patchUser(1, patch)).thenReturn(patchedUserDto);
		mvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(patch)))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo("updated User A")),
						jsonPath("$.email", equalTo("user_a@mail.org"))
				);
	}

	@Test
	@SneakyThrows
	void patchUser_whenPatchHasId() {
		// given
		Map<String, String> patch = new HashMap<>();
		patch.put("id", "100");

		// when
		mvc.perform(patch("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(patch)))

				// then
				.andExpect(status().isBadRequest());
	}

	@Test
	@SneakyThrows
	void deleteUser() {
		// when
		mvc.perform(delete("/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))

				// then
				.andExpect(status().isOk());
		verify(userService).deleteUserById(1);
	}

	@Test
	@SneakyThrows
	void getUserById_whenUserFound_thenUserReturned() {
		// given
		UserDto responseDto = UserDto.builder()
				.name("User A")
				.email("user_a@mail.org")
				.build();

		when(userService.getUserById(anyInt())).thenReturn(responseDto);
		// when
		mvc.perform(get("/users/1")
						.accept(MediaType.APPLICATION_JSON))

				// then
				.andExpect(status().isOk());
		verify(userService).getUserById(anyInt());
	}

	@Test
	@SneakyThrows
	void getUserById_whenUserNotFound_thenThrowResourceNotFoundException() {
		doThrow(ResourceNotFoundException.class).when(userService).getUserById(anyInt());
		// when
		mvc.perform(get("/users/1")
						.accept(MediaType.APPLICATION_JSON))

				// then
				.andExpect(status().isNotFound());
		verify(userService).getUserById(anyInt());
	}

	@Test
	@SneakyThrows
	void getUsers() {
		// given
		UserDto user1 = UserDto.builder().name("User 1").email("user_1@mail.org").build();
		UserDto user2 = UserDto.builder().name("User 2").email("user_2@mail.org").build();

		List<UserDto> users = List.of(user1, user2);
		// when
		when(userService.getUsers()).thenReturn(users);

		mvc.perform(get("/users")
						.accept(MediaType.APPLICATION_JSON))
				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(2)),
						jsonPath("$..name", hasItems("User 1", "User 2"))
				);
	}

}