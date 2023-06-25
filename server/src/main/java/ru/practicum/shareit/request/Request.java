package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REQUESTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REQUEST_ID")
	@EqualsAndHashCode.Include
	private int id;
	private String description;
	@ManyToOne
	@JoinColumn(name = "REQUESTER_ID", nullable = false)
	private User requester;
	private LocalDateTime created;
}
