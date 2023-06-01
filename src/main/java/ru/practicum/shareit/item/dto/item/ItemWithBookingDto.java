package ru.practicum.shareit.item.dto.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.repository.ItemWithBookingProjection;

@Data
@NoArgsConstructor
public class ItemWithBookingDto {
	private Integer id;
	private String name;
	private String description;
	private Boolean available;
	private BookingShort lastBooking;
	private BookingShort nextBooking;

	public ItemWithBookingDto(ItemWithBookingProjection projection) {
		this.id = projection.getId();
		this.name = projection.getName();
		this.description = projection.getDescription();
		this.available = projection.getAvailable();
		if (projection.getLastBookingId() != null) {
			lastBooking = new BookingShort(projection.getLastBookingId(), projection.getLastBookingBookerId());
		}
		if (projection.getNextBookingId() != null) {
			nextBooking = new BookingShort(projection.getNextBookingId(), projection.getNextBookingBookerId());
		}
	}
}