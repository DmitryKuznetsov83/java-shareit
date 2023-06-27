package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

	private final BookingService bookService;
	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public BookingResponseDto postBooking(@RequestBody BookingRequestDto bookingRequestDto,
	                                      @RequestHeader(USER_HEADER) Integer bookerId) {
		return bookService.addBooking(bookingRequestDto, bookerId);
	}

	@PatchMapping("/{bookingId}")
	public BookingResponseDto approveBooking(@PathVariable Integer bookingId,
	                                         @RequestHeader(USER_HEADER) Integer ownerId,
	                                         @RequestParam boolean approved) {
		return bookService.approveBooking(bookingId, ownerId, approved);
	}

	@GetMapping("/{bookingId}")
	public BookingResponseDto getBooking(@PathVariable Integer bookingId,
	                                     @RequestHeader(USER_HEADER) Integer userId) {
		return bookService.getBookingById(bookingId, userId);
	}

	@GetMapping()
	public List<BookingResponseDto> getBookersBookings(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
	                                                   @RequestParam(required = false) Integer from,
	                                                   @RequestParam(required = false) Integer size,
	                                                   @RequestHeader(USER_HEADER) Integer bookerId) {
		return bookService.getBookersBookings(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public List<BookingResponseDto> getOwnersBookings(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
	                                                  @RequestParam(required = false) Integer from,
	                                                  @RequestParam(required = false) Integer size,
	                                                  @RequestHeader(USER_HEADER) Integer ownerId) {
		return bookService.getOwnersBookings(ownerId, state, from, size);
	}

}