package ru.practicum.shareit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

	private final String status;
	private final LocalDateTime timestamp;
	private final String error;
	private final String description;

	public ErrorResponse(HttpStatus status, String error, String description) {
		this.status = Integer.toString(status.value());
		this.timestamp = LocalDateTime.now();
		this.error = error;
		this.description = description;
	}

}
