package ru.practicum.shareit.booking.exception;

public class SelfBookingException extends RuntimeException {
	public SelfBookingException() {
		super("One can't book his item");
	}
}
