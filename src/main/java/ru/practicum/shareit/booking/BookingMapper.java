package ru.practicum.shareit.booking;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
	private static final ModelMapper modelMapper = new ModelMapper();

	public static Booking mapToNewBooking(BookingRequestDto bookingRequestDto, User booker, Item item) {
		return Booking.builder()
				.start(bookingRequestDto.getStart())
				.end(bookingRequestDto.getEnd())
				.item(item)
				.booker(booker)
				.status(BookingStatus.WAITING)
				.build();
	}

	public static BookingResponseDto mapToBookingDto(Booking booking) {
		return modelMapper.map(booking, BookingResponseDto.class);
	}

	public static List<BookingResponseDto> mapToBookingDtoList(List<Booking> bookingList) {
		return bookingList.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());

	}

	public static BookingShortResponseDto mapToBookingShortDto(Booking booking) {
		return modelMapper.map(booking, BookingShortResponseDto.class);
	}

}
