package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aop.ErrorResponse;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.enums.BookingState;
import ru.practicum.shareit.exception.UnknownBookingStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public ResponseEntity<Object> postBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
	                                          @RequestHeader(USER_HEADER) @Positive Integer bookerId) {
		log.info("Create booking {}, userId={}", bookingRequestDto, bookerId);
		return bookingClient.postBooking(bookingRequestDto, bookerId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable @Positive Integer bookingId,
	                                         @RequestHeader(USER_HEADER) @Positive Integer ownerId,
	                                         @RequestParam boolean approved) {
		log.info("Approve booking {}, ownerId={}", bookingId, ownerId);
		return bookingClient.approveBooking(bookingId, ownerId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@PathVariable @Positive Integer bookingId,
	                                     @RequestHeader(USER_HEADER) @Positive Integer userId) {
		log.info("Get booking by id {}, userId={}", bookingId, userId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping()
	public ResponseEntity<Object> getBookersBookings(@RequestParam(defaultValue = "ALL") BookingState state,
	                                                   @RequestParam(required = false) @PositiveOrZero Integer from,
	                                                   @RequestParam(required = false) @Positive Integer size,
	                                                   @RequestHeader(USER_HEADER) @Positive Integer bookerId) {
		log.info("Get booking with state {}, bookerId={}, from={}, size={}", state, bookerId, from, size);
		return bookingClient.getBookersBookings(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnersBookings(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
	                                                  @RequestParam(required = false) @PositiveOrZero Integer from,
	                                                  @RequestParam(required = false) @Positive Integer size,
	                                                  @RequestHeader(USER_HEADER) @Positive Integer ownerId) {
		log.info("Get booking with state {}, ownerId={}, from={}, size={}", state, ownerId, from, size);
		return bookingClient.getOwnersBookings(ownerId, state, from, size);
	}

	@ExceptionHandler({UnknownBookingStateException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	private ResponseEntity<Object> handleUnknownBookingStateException(final UnknownBookingStateException e) {
		log.warn("Bad query: {}", e.getMessage());
		Object body = new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "Invalid booking state");
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

}