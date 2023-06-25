package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer>, QuerydslPredicateExecutor<Booking> {

	List<Booking> findAllByItemAndBookerAndStatusAndEndIsLessThanOrderByStartDesc(Item item, User booker, BookingStatus bookingStatus, LocalDateTime now);

	String findLastAndNextBookingQuery =
			"SELECT * FROM BOOKINGS " +
					"WHERE ITEM_ID = :itemId AND START_DATE <= :now AND STATUS = 'APPROVED' AND START_DATE = " +
					"(SELECT MAX(START_DATE) FROM BOOKINGS " +
					"WHERE ITEM_ID = :itemId AND START_DATE <= :now AND STATUS = 'APPROVED')\n" +
			"UNION ALL\n" +
			"SELECT * FROM BOOKINGS " +
					"WHERE ITEM_ID = :itemId AND START_DATE >= :now AND STATUS = 'APPROVED' AND START_DATE = " +
					"(SELECT MIN(START_DATE) FROM BOOKINGS " +
					"WHERE ITEM_ID = :itemId AND START_DATE >= :now AND STATUS = 'APPROVED')";

	@Query(nativeQuery = true, value = findLastAndNextBookingQuery)
	List<Booking> findLastAndNextBooking(Integer itemId, LocalDateTime now);

	List<Booking> findAllByItemOwnerId(Integer ownerId);

	List<Booking> findAllByItemIn(List<Item> itemList);

}
