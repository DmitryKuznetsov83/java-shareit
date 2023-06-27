package ru.practicum.shareit.item.entity;

import lombok.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Table(name = "ITEMS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ITEM_ID")
	@EqualsAndHashCode.Include
	private Integer id;
	private String name;
	private String description;
	private Boolean available;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER_ID", nullable = false)
	private User owner;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REQUEST_ID")
	private Request request;
}
