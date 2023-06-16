package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aop.ErrorResponse;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.exception.IncorrectStatusChangeException;
import ru.practicum.shareit.booking.exception.ResourceNotAvailableException;
import ru.practicum.shareit.booking.exception.SelfBookingException;
import ru.practicum.shareit.booking.exception.UnknownBookingStateException;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

	private final BookingService bookService;
	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	@Validated
	public BookingResponseDto postBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
	                                      @RequestHeader(USER_HEADER) @Positive Integer bookerId) {
		return bookService.addBooking(bookingRequestDto, bookerId);
	}

	@PatchMapping("/{bookingId}")
	public BookingResponseDto approveBooking(@PathVariable @Positive Integer bookingId,
	                                         @RequestHeader(USER_HEADER) @Positive Integer ownerId,
	                                         @RequestParam boolean approved) {
		return bookService.approveBooking(bookingId, ownerId, approved);
	}

	@GetMapping("/{bookingId}")
	public BookingResponseDto getBooking(@PathVariable @Positive Integer bookingId,
	                                     @RequestHeader(USER_HEADER) @Positive Integer userId) {
		return bookService.getBookingById(bookingId, userId);
	}

	@GetMapping()
	public List<BookingResponseDto> getBookersBookings(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
	                                                   @RequestParam(required = false) @PositiveOrZero Integer from,
	                                                   @RequestParam(required = false) @Positive Integer size,
	                                                   @RequestHeader(USER_HEADER) @Positive Integer bookerId) {
		return bookService.getBookersBookings(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public List<BookingResponseDto> getOwnersBookings(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
	                                                  @RequestParam(required = false) @PositiveOrZero Integer from,
	                                                  @RequestParam(required = false) @Positive Integer size,
	                                                  @RequestHeader(USER_HEADER) @Positive Integer ownerId) {
		return bookService.getOwnersBookings(ownerId, state, from, size);
	}

	@ExceptionHandler({ResourceNotAvailableException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	private ErrorResponse handleItemNotAvailableException(final ResourceNotAvailableException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Conflict operation", e.getMessage());
	}

	@ExceptionHandler({IncorrectStatusChangeException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	private ErrorResponse handleIncorrectStatusChangeException(final IncorrectStatusChangeException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Forbidden operation", e.getMessage());
	}

	@ExceptionHandler({UnknownBookingStateException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	private ErrorResponse handleUnsupportedBookingStateStatusException(final UnknownBookingStateException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "");
	}

	@ExceptionHandler({SelfBookingException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	private ErrorResponse handleSelfBookingException(final SelfBookingException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.NOT_FOUND, "Forbidden operation", e.getMessage());
	}

}
