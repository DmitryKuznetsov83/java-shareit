package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.item.repository.ItemWithBookingProjection;

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

	public ItemWithBookingResponseDto(ItemWithBookingProjection projection) {
		this.id = projection.getId();
		this.name = projection.getName();
		this.description = projection.getDescription();
		this.available = projection.getAvailable();
		if (projection.getLastBookingId() != null) {
			lastBooking = new BookingShortResponseDto(projection.getLastBookingId(), projection.getLastBookingBookerId());
		}
		if (projection.getNextBookingId() != null) {
			nextBooking = new BookingShortResponseDto(projection.getNextBookingId(), projection.getNextBookingBookerId());
		}
	}
}