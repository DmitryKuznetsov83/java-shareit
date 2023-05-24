package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
	private Integer id;
	private String name;
	private String email;
}
