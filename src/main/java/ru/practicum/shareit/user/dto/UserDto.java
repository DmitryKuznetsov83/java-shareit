package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
public class UserDto {
	private Integer id;
	@NotNull
	@NotBlank
	private String name;
	@NotNull
	@Email
	@NotBlank
	private String email;
}