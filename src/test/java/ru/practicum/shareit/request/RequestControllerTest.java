package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

	@MockBean
	RequestService requestService;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Test
	@SneakyThrows
	void postRequest() {
		// given
		RequestRequestDto requestRequestDto = RequestRequestDto.builder()
				.description("Item A request")
				.build();

		RequestResponseDto requestResponseDto = RequestResponseDto.builder()
				.description("Item A request")
				.build();

		// when
		when(requestService.addRequest(any(), anyInt())).thenReturn(requestResponseDto);

		mvc.perform(post("/requests")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(requestRequestDto)))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.description", equalTo("Item A request"))
				);

	}

	@Test
	@SneakyThrows
	void getRequest() {
		// given
		RequestResponseDto requestResponseDto = RequestResponseDto.builder()
				.description("Item A request")
				.build();

		when(requestService.getRequestById(anyInt(), anyInt())).thenReturn(requestResponseDto);
		// when
		mvc.perform(get("/requests/1")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.description", equalTo("Item A request"))
				);
	}

	@Test
	@SneakyThrows
	void getOwnRequests() {
		mvc.perform(get("/requests")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(0))
				);
	}

	@Test
	@SneakyThrows
	void getOthersRequests() {
		mvc.perform(get("/requests/all")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(0))
				);
	}

	@Test
	@SneakyThrows
	void getOthersRequests_whenIncorrectPaging() {
		mvc.perform(get("/requests/all")
						.accept(MediaType.APPLICATION_JSON)
						.param("from", "-100")
						.param("size", "10")
						.header("X-Sharer-User-Id", 1))

				// then
				.andExpectAll(
						status().isBadRequest()
				);
	}


}