package ru.practicum.shareit.booking.annotation;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartEndFieldsValidator implements ConstraintValidator<StartEndFields, BookingRequestDto> {

	@Override
	public void initialize(StartEndFields startEndFields) {
	}

	@Override
	public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext constraintValidatorContext) {
		LocalDateTime start = bookingRequestDto.getStart();
		LocalDateTime end = bookingRequestDto.getEnd();
		if (start == null || end == null) {
			return false;
		}
		return start.isBefore(end);
	}

}