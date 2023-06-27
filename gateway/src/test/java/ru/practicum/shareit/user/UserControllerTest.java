package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

	@MockBean
	UserClient userClient;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Test
	@SneakyThrows
	void postUser_whenBadEmail_thenStatusIsBadRequest() {
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
	void patchUser_whenPatchHasId_thenStatusIsBadRequest() {
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

}
