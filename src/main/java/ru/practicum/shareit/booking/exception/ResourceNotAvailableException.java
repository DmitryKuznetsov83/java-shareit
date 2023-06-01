package ru.practicum.shareit.booking.exception;

public class ResourceNotAvailableException extends RuntimeException {
	public ResourceNotAvailableException(String resourceName, Integer id) {
		super("Resource " + resourceName + " with id " + id + " not available");
	}

}
