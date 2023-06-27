package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.aop.OnCreate;
import ru.practicum.shareit.aop.OnUpdate;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
	@Null(groups = OnCreate.class)
	private Integer id;
	@NotNull(groups = OnCreate.class)
	@NotBlank(groups = {OnCreate.class, OnUpdate.class})
	private String name;
	@NotBlank(groups = OnCreate.class)
	@Email(groups = {OnCreate.class, OnUpdate.class})
	private String email;
}
