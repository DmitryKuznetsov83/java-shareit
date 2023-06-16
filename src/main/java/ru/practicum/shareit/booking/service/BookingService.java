package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface BookingService {

	BookingResponseDto addBooking(BookingRequestDto bookingRequestDto, Integer ownerId);

	BookingResponseDto approveBooking(Integer bookingId, Integer userId, boolean approved);

	BookingResponseDto getBookingById(Integer bookingId, Integer userId);

	List<BookingResponseDto> getBookersBookings(Integer bookerId, BookingState state, Integer from, Integer size);

	List<BookingResponseDto> getOwnersBookings(Integer ownerId, BookingState state, Integer from, Integer size);

	List<Booking> getLastAndNextBookingOfItem(Integer itemId);

	List<Booking> getFinishedBookingsByItemAndBooker(Item item, User booker);

}