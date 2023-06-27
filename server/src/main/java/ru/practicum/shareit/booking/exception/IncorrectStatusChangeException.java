package ru.practicum.shareit.booking.exception;

public class IncorrectStatusChangeException extends RuntimeException {
	public IncorrectStatusChangeException() {
		super("Incorrect status change");
	}
}
