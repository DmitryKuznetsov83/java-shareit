package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKINGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@Column(name = "BOOKING_ID")
	private Integer id;
	@Column(name = "START_DATE")
	private LocalDateTime start;
	@Column(name = "END_DATE")
	private LocalDateTime end;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_ID", nullable = false)
	private Item item;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOOKER_ID", nullable = false)
	private User booker;
	@Enumerated(EnumType.STRING)
	private BookingStatus status;

}