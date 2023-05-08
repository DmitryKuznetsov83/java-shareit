package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@NoArgsConstructor
public class Item {
	private Integer id;
	private String name;
	private String description;
	private Boolean available;
	private User owner;
}
