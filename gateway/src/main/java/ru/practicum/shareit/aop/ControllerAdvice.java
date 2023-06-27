package ru.practicum.shareit.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.UnknownBookingStateException;

import javax.validation.ConstraintViolationException;


@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

	@ExceptionHandler(ResourceAccessException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ErrorResponse handleUnexpectedError(final ResourceAccessException e) {
		log.warn("Shareit server unavailable", e.getMessage());
		return new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,"Service unavailable", e.getMessage());
	}

	@ExceptionHandler(IncorrectIdException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleIncorrectIdException(final IncorrectIdException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST, "Incorrect id", e.getMessage());
	}

	@ExceptionHandler({UnknownBookingStateException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	private ErrorResponse handleUnknownBookingStateException(final UnknownBookingStateException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "Invalid booking state");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Validation error", e.getMessage());
	}

	@ExceptionHandler({MissingRequestHeaderException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Header X-Sharer-User-Id is empty", e.getMessage());
	}

	@ExceptionHandler({ConstraintViolationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Constraint violation", e.getMessage());
	}

	@ExceptionHandler({MissingServletRequestParameterException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMissingServletRequestParameterExceptionn(final MissingServletRequestParameterException e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Missing request parameter", e.getMessage());
	}

	@ExceptionHandler({Throwable.class})
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleThrowable(final Throwable e) {
		log.warn("Bad query: {}", e.getMessage());
		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"internal server error", e.getMessage());
	}

}