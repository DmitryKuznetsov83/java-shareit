package ru.practicum.shareit.item.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CommentCreationDto {
	@NotNull
	@NotBlank
	private String text;
}
