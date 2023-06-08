package ru.practicum.shareit.booking.exception;

public class UnknownBookingStateException extends RuntimeException {
	public UnknownBookingStateException(String unknownState) {
		super("Unknown state: " + unknownState);
	}

}
