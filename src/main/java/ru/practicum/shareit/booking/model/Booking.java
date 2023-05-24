package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class Booking {
	private int id;
	@NotNull
	private LocalDateTime start;
	@NotNull
	private LocalDateTime end;
	@NotNull
	private Item item;
	@NotNull
	private User booker;
	@NotNull
	private BookingStatus status;
}
