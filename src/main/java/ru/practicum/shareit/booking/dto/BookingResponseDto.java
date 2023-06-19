package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.UserShortResponseDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDto {

	public Integer id;
	public ItemShortResponseDto item;
	public LocalDateTime start;
	public LocalDateTime end;
	public BookingStatus status;
	public UserShortResponseDto booker;

}