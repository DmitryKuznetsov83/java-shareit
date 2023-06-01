package ru.practicum.shareit.booking.annotation;

import ru.practicum.shareit.booking.dto.BookingCreationDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartEndFieldsValidator implements ConstraintValidator<StartEndFields, BookingCreationDto> {

	@Override
	public void initialize(StartEndFields startEndFields) {
	}

	@Override
	public boolean isValid(BookingCreationDto bookingCreationDto, ConstraintValidatorContext constraintValidatorContext) {
		LocalDateTime start = bookingCreationDto.getStart();
		LocalDateTime end = bookingCreationDto.getEnd();
		if (start == null || end == null) {
			return false;
		}
		return start.isBefore(end);
	}

}