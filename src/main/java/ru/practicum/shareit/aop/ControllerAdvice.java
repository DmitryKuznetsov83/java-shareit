package ru.practicum.shareit.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

	// CUSTOM EXCEPTIONS
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleIncorrectIdException(final IncorrectIdException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrect id", e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleRecourseNotFoundException(final ResourceNotFoundException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.NOT_FOUND,"Resource not found", e.getMessage());
	}


	// INTERNAL EXCEPTIONS
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.CONFLICT,"Data integrity violation exception", e.getMessage());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Validation error", e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Validation error", e.getMessage());
	}

	@ExceptionHandler({UnauthorizedChangeException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleMissingRequestHeaderException(final UnauthorizedChangeException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.NOT_FOUND,"Forbidden operation", e.getMessage());
	}

	@ExceptionHandler({MissingRequestHeaderException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Header X-Sharer-User-Id is empty", e.getMessage());
	}

	@ExceptionHandler({UnauthorizedCommentException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleUnauthorizedCommentException(final UnauthorizedCommentException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Forbidden operation", e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleUnexpectedError(final Exception e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected error", e.getMessage());
	}

}