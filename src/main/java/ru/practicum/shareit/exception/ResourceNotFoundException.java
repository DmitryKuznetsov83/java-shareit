package ru.practicum.shareit.exception;


public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	public ResourceNotFoundException(String resourceName, Integer id) {
		super(resourceName + " with id " + id + " not found");
	}

}
