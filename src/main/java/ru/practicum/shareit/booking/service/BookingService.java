package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

	BookingDto addBooking(BookingCreationDto bookingCreationDto, Integer ownerId);

	BookingDto approveBooking(Integer bookingId, Integer userId, boolean approved);

	BookingDto getBookingById(Integer bookingId, Integer userId);

	List<BookingDto> getBookersBookings(Integer bookerId, BookingState state);

	List<BookingDto> getOwnersBookings(Integer ownerId, BookingState state);

}