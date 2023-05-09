package ru.practicum.shareit.exception;

public class EmailNameAlreadyUsedException extends RuntimeException {
	public EmailNameAlreadyUsedException(String msg) {
		super(msg);
	}
}
