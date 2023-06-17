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
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exception.IncorrectStatusChangeException;
import ru.practicum.shareit.booking.exception.ResourceNotAvailableException;
import ru.practicum.shareit.booking.exception.SelfBookingException;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.item.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserShortResponseDto;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

	@MockBean
	BookingService bookingService;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	private BookingRequestDto bookingRequestDto;
	private ItemShortResponseDto itemShortResponseDto;
	private UserShortResponseDto userShortResponseDto;
	private BookingResponseDto bookingResponseDto;

	@BeforeEach
	void setUp() {
		bookingRequestDto = BookingRequestDto.builder()
				.itemId(1)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.build();

		itemShortResponseDto = ItemShortResponseDto.builder()
				.name("Item A")
				.build();

		userShortResponseDto = UserShortResponseDto.builder()
				.id(1)
				.build();

		bookingResponseDto = BookingResponseDto.builder()
				.item(itemShortResponseDto)
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.status(BookingStatus.WAITING)
				.booker(userShortResponseDto)
				.build();
	}

	@Test
	@SneakyThrows
	void postBooking_whenItemIsAvailable_thenBookingPosted() {
		// when
		when(bookingService.addBooking(bookingRequestDto, 1)).thenReturn(bookingResponseDto);

		mvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(bookingRequestDto)))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.status", equalTo("WAITING")),
						jsonPath("$.item.name", equalTo("Item A"))
				);

	}

	@Test
	@SneakyThrows
	void postBooking_whenItemIsNotAvailable_thenItemNotAvailableExceptionThrown() {
		// when
		when(bookingService.addBooking(any(), anyInt())).thenThrow(new ResourceNotAvailableException("Item", 1));
		mvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(bookingRequestDto)))

				// then
				.andExpectAll(
						status().isBadRequest()
				);

	}

	@Test
	@SneakyThrows
	void postBooking_whenBookerIsOwner_thenSelfBookingExceptionThrown() {
		// when
		when(bookingService.addBooking(any(), anyInt())).thenThrow(new SelfBookingException());

		mvc.perform(post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(bookingRequestDto)))

				// then
				.andExpectAll(
						status().isNotFound()
				);

	}

	@Test
	@SneakyThrows
	void postBooking_whenNoBookerHeader_thenMissingRequestHeaderExceptionThrown() {
		// when
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
	void approveBooking_whenStatusIsWaiting_thenBookingApproved() {
		// given
		bookingResponseDto.setStatus(BookingStatus.APPROVED);

		// when
		when(bookingService.approveBooking(1, 1, true)).thenReturn(bookingResponseDto);

		mvc.perform(patch("/bookings/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", "1")
						.param("approved", "true"))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.status", equalTo("APPROVED")),
						jsonPath("$.item.name", equalTo("Item A"))
				);

	}

	@Test
	@SneakyThrows
	void approveBooking_whenStatusIsNotWaiting_thenIncorrectStatusChangeExceptionThrown() {
		// when
		when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenThrow(new IncorrectStatusChangeException());

		mvc.perform(patch("/bookings/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", "1")
						.param("approved", "true"))

				// then
				.andExpectAll(
						status().isBadRequest()
				);

	}

	@Test
	@SneakyThrows
	void getBooking_whenBookingFound_thenBookingReturned() {
		// when
		when(bookingService.getBookingById(1, 1)).thenReturn(bookingResponseDto);

		mvc.perform(get("/bookings/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", "1"))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.status", equalTo("WAITING")),
						jsonPath("$.item.name", equalTo("Item A"))
				);
	}

	@Test
	@SneakyThrows
	void getBookersBookings_whenStateIsCorrect_thenStatusIsOk() {
		// when
		mvc.perform(get("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("state", "ALL")
						.header("X-Sharer-User-Id", "1"))

				// then
				.andExpectAll(
						status().isOk()
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

	@Test
	@SneakyThrows
	void getOwnersBookings_whenStateIsCorrect_thenStatusIsOk() {
		// when
		mvc.perform(get("/bookings/owner")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("state", "ALL")
						.header("X-Sharer-User-Id", "1"))

				// then
				.andExpectAll(
						status().isOk()
				);
	}

}