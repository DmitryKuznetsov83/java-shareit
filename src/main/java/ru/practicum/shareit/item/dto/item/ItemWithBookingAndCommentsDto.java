package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemWithBookingAndCommentsDto {
	private Integer id;
	private String name;
	private String description;
	private Boolean available;
	private BookingShort lastBooking;
	private BookingShort nextBooking;
	private List<ItemDetailedCommentDto> comments;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemDetailedCommentDto {
		private Integer id;
		private String text;
		private String authorName;
		private LocalDateTime created;
	}

}