package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	@EqualsAndHashCode.Include
	private Integer id;
	private String name;
	private String email;
}