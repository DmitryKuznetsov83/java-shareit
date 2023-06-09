package ru.practicum.shareit.booking.enums;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.exception.UnknownBookingStateException;

@Component
public class StringToBookingStateEnumConverter implements Converter<String, BookingState> {
	@Override
	public BookingState convert(String state) {
		try {
			return BookingState.valueOf(state.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new UnknownBookingStateException(state);
		}
	}
}
