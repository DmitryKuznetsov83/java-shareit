package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemWithBookingAndCommentsResponseDto {
	private Integer id;
	private String name;
	private String description;
	private Boolean available;
	private BookingShortResponseDto lastBooking;
	private BookingShortResponseDto nextBooking;
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