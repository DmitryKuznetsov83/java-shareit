package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDto {
	public Integer id;
	public BookingItemDto item;
	public LocalDateTime start;
	public LocalDateTime end;
	public BookingStatus status;
	public BookingBookerDto booker;
}

@Data
@NoArgsConstructor
class BookingBookerDto {
	public Integer id;
}

@Data
@NoArgsConstructor
class BookingItemDto {
	public Integer id;
	public String name;
}