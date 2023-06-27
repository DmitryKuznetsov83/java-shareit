package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemWithBookingResponseDto {
	private Integer id;
	private String name;
	private String description;
	private Boolean available;
	private BookingShortResponseDto lastBooking;
	private BookingShortResponseDto nextBooking;
}