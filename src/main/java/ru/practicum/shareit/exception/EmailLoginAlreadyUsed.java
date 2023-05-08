package ru.practicum.shareit.exception;

public class EmailLoginAlreadyUsed extends RuntimeException {
	public EmailLoginAlreadyUsed(String msg) {
		super(msg);
	}
}
