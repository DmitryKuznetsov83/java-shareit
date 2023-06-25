package ru.practicum.shareit.exception;

public class UnknownBookingStateException extends RuntimeException {
	public UnknownBookingStateException(String unknownState) {
		super("Unknown state: " + unknownState);
	}

}
