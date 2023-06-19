package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.aop.OnCreate;
import ru.practicum.shareit.aop.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
	@Null(groups = OnCreate.class)
	@NotNull(groups = OnUpdate.class)
	private Integer id;
	@NotNull
	@NotBlank
	private String name;
	@NotNull
	@NotBlank
	private String description;
	@NotNull
	private Boolean available;
	private Integer requestId;
}