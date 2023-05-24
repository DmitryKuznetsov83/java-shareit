package ru.practicum.shareit.exception;

public class UnauthorizedItemChangeException extends RuntimeException {
	public UnauthorizedItemChangeException(String msg) {
		super(msg);
	}
}
