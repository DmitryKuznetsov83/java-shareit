package ru.practicum.shareit.aop;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.ResourceNotFoundException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {

	// CUSTOM EXCEPTIONS
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleIncorrectIdException(final IncorrectIdException e) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrect id", e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleRecourseNotFoundException(final ResourceNotFoundException e) {
		return new ErrorResponse(HttpStatus.NOT_FOUND,"Resource not found", e.getMessage());
	}


	// INTERNAL EXCEPTIONS
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Validation error", e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Validation error", e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleUnexpectedError(final Exception e) {
		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected error", e.getMessage());
	}

}