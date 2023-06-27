package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

	@MockBean
	BookingClient bookingClient;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	private BookingRequestDto bookingRequestDto;

	@BeforeEach
	void setUp() {
		bookingRequestDto = BookingRequestDto.builder()
				.itemId(1)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build();
	}

	@Test
	@SneakyThrows
	void postBooking_whenNoBookerHeader_thenMissingRequestHeaderExceptionThrown() {
		mvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingRequestDto)))

				// then
				.andExpectAll(
						status().isBadRequest()
				);

	}

	@Test
	@SneakyThrows
	void getBookersBookings_whenStateIsIncorrect_thenStatusIsBadRequest() {
		// when
		mvc.perform(get("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("state", "IncorrectState")
						.header("X-Sharer-User-Id", "1"))

				// then
				.andExpectAll(
						status().isBadRequest()
				);
	}

}
