package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingJpaRepositoryTest {

	@Autowired
	private TestEntityManager em;
	@Autowired
	private BookingJpaRepository repository;

	private User owner;
	private User booker;
	private Item item;

	LocalDateTime lbStart;
	LocalDateTime lbEnd;
	LocalDateTime nbStart;
	LocalDateTime nbEnd;
	LocalDateTime anbStart;
	LocalDateTime anbEnd;


	@BeforeEach
	void setUp() {
		owner = User.builder().name("Owner").email("owner@mail.org").build();
		booker = User.builder().name("Booker").email("booker@mail.org").build();
		item = Item.builder().name("Item A").description("Item A").available(true).owner(owner).build();

		lbStart = LocalDateTime.now().minusDays(10);
		lbEnd = LocalDateTime.now().minusDays(9);
		nbStart = LocalDateTime.now().plusDays(9);
		nbEnd = LocalDateTime.now().plusDays(10);
		anbStart = LocalDateTime.now().plusDays(19);
		anbEnd = LocalDateTime.now().plusDays(20);
	}

	@Test
	@Transactional
	void findLastAndNextBooking_whenNoBookings_thenReturnEmptyList() {
		// given
		em.persist(owner);
		em.persist(booker);
		Item persistedItem = em.persist(item);
		// when
		List<Booking> bookingList = repository.findLastAndNextBooking(persistedItem.getId(), LocalDateTime.now());
		// then
		assertTrue(bookingList.isEmpty());
	}

	@Test
	@Transactional
	void findLastAndNextBooking_whenLastBooking_thenReturnLastBookingOnly() {
		// given
		em.persist(owner);
		em.persist(booker);
		Item persistedItem = em.persist(item);
		Booking lastBooking = Booking.builder()
				.start(lbStart).end(lbEnd).item(persistedItem).booker(booker).status(BookingStatus.APPROVED).build();
		em.persist(lastBooking);
		// when
		List<Booking> bookingList = repository.findLastAndNextBooking(persistedItem.getId(), LocalDateTime.now());
		// then
		assertThat(bookingList, containsInAnyOrder(lastBooking));
	}

	@Test
	@Transactional
	void findLastAndNextBooking_whenLastAndNext_thenReturnLastAndNextBookings() {
		// given
		em.persist(owner);
		em.persist(booker);
		Item persistedItem = em.persist(item);
		Booking lastBooking = Booking.builder()
				.start(lbStart).end(lbEnd).item(persistedItem).booker(booker).status(BookingStatus.APPROVED).build();
		Booking nextBooking = Booking.builder()
				.start(nbStart).end(nbEnd).item(persistedItem).booker(booker).status(BookingStatus.APPROVED).build();
		em.persist(lastBooking);
		em.persist(nextBooking);
		// when
		List<Booking> bookingList = repository.findLastAndNextBooking(persistedItem.getId(), LocalDateTime.now());
		// then
		assertThat(bookingList, containsInAnyOrder(lastBooking, nextBooking));
	}

	@Test
	@Transactional
	void findLastAndNextBooking_whenLastAndNextAndAfterNextBooking_thenReturnLastAndNextBooking() {
		// given
		em.persist(owner);
		em.persist(booker);
		Item persistedItem =  em.persist(item);
		Booking lastBooking = Booking.builder()
				.start(lbStart).end(lbEnd).item(persistedItem).booker(booker).status(BookingStatus.APPROVED).build();
		Booking nextBooking = Booking.builder()
				.start(nbStart).end(nbEnd).item(persistedItem).booker(booker).status(BookingStatus.APPROVED).build();
		Booking afterNextBooking = Booking.builder()
				.start(anbStart).end(anbEnd).item(persistedItem).booker(booker).status(BookingStatus.APPROVED).build();
		em.persist(lastBooking);
		em.persist(nextBooking);
		em.persist(afterNextBooking);
		// when
		List<Booking> bookingList = repository.findLastAndNextBooking(persistedItem.getId(), LocalDateTime.now());
		// then
		assertThat(bookingList, containsInAnyOrder(lastBooking, nextBooking));
	}

}