package ru.practicum.shareit.exception;

public class UnauthorizedChangeException extends RuntimeException {
	public UnauthorizedChangeException(String resourceName, Integer id) {
		super("Unauthorized change of " + resourceName + " with id " + id);
	}
}
