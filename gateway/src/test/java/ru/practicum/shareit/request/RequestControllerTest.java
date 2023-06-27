package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

	@MockBean
	RequestClient requestClient;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Test
	@SneakyThrows
	void getOthersRequests_whenIncorrectPaging_thenStatusIsBadRequest() {
		// when
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
