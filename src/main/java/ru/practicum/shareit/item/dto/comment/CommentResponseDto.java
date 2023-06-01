package ru.practicum.shareit.item.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentResponseDto {
	private Integer id;
	private String text;
	private String authorName;
	private LocalDateTime created;
}
