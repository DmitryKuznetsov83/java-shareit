package ru.practicum.shareit.booking;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.item.BookingShort;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
	private static final ModelMapper modelMapper = new ModelMapper();

	public static Booking mapToNewBooking(BookingCreationDto bookingCreationDto, User booker, Item item) {
		return Booking.builder()
				.start(bookingCreationDto.getStart())
				.end(bookingCreationDto.getEnd())
				.item(item)
				.booker(booker)
				.status(BookingStatus.WAITING)
				.build();
	}

	public static BookingDto mapToBookingDto(Booking booking) {
		return modelMapper.map(booking, BookingDto.class);
	}

	public static List<BookingDto> mapToBookingDtoList(List<Booking> bookingList) {
		return bookingList.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());

	}

	public static BookingShort mapToBookingShortDto(Booking booking) {
		return modelMapper.map(booking, BookingShort.class);
	}
}
