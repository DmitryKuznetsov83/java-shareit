package ru.practicum.shareit.item.entity;

import com.querydsl.core.annotations.QueryExclude;
import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@QueryExclude
@Table(name = "KOMMENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "KOMMENT_ID")
	@EqualsAndHashCode.Include
	private Integer id;
	private String text;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID", nullable = false)
	private Item item;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTHOR_ID", nullable = false)
	private User author;
	@Builder.Default
	private LocalDateTime created = LocalDateTime.now();

}
