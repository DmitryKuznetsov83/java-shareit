package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.Request;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Integer> {

	List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String nameSearch,
	                                                                                             String descSearch);

	String sqlFindByOwnerWithBookingDto = "SELECT\n" +
			"    ITEMS_BOOKINGS.ID AS id,\n" +
			"    ITEMS_BOOKINGS.NAME AS name,\n" +
			"    ITEMS_BOOKINGS.DESCRIPTION AS description,\n" +
			"    ITEMS_BOOKINGS.AVAILABLE AS available,\n" +
			"    ITEMS_BOOKINGS.LASTBOOKING AS lastBookingId,\n" +
			"    ITEMS_BOOKINGS.NEXTBOOKING AS nextBookingId,\n" +
			"    B1.BOOKER_ID AS lastBookingBookerId,\n" +
			"    B2.BOOKER_ID AS nextBookingBookerId\n" +
			"FROM (\n" +
			"    SELECT\n" +
			"        I.ITEM_ID AS ID,\n" +
			"        I.NAME,\n" +
			"        I.DESCRIPTION,\n" +
			"        I.AVAILABLE,\n" +
			"        (SELECT\n" +
			"             MAX(BOOKING_ID)\n" +
			"         FROM\n" +
			"             BOOKINGS\n" +
			"         WHERE\n" +
			"             START_DATE =\n" +
			"                 (SELECT\n" +
			"                      MAX(START_DATE)\n" +
			"                 FROM\n" +
			"                      BOOKINGS\n" +
			"                 WHERE\n" +
			"                     STATUS = 'APPROVED' AND START_DATE <= :now)\n" +
			"             AND BOOKINGS.ITEM_ID = I.ITEM_ID) AS LASTBOOKING,\n" +
			"        (SELECT\n" +
			"                 MIN(BOOKING_ID)\n" +
			"             FROM\n" +
			"                 BOOKINGS\n" +
			"             WHERE\n" +
			"                 START_DATE =\n" +
			"                     (SELECT\n" +
			"                          MIN(START_DATE)\n" +
			"                     FROM\n" +
			"                          BOOKINGS\n" +
			"                     WHERE\n" +
			"                         STATUS = 'APPROVED' AND START_DATE >= :now)\n" +
			"                 AND BOOKINGS.ITEM_ID = I.ITEM_ID) AS NEXTBOOKING\n" +
			"FROM\n" +
			"    ITEMS AS I\n" +
			"WHERE\n" +
			"    OWNER_ID = :ownerId) AS ITEMS_BOOKINGS\n" +
			"         LEFT JOIN BOOKINGS AS B1 ON B1.BOOKING_ID = ITEMS_BOOKINGS.LASTBOOKING\n" +
			"         LEFT JOIN BOOKINGS AS B2 ON B2.BOOKING_ID = ITEMS_BOOKINGS.NEXTBOOKING\n" +
			"ORDER BY\n" +
			"    ID";

	@Query(nativeQuery = true, value = sqlFindByOwnerWithBookingDto)
	List<ItemWithBookingProjection> findByOwnerWithBookingDto(Integer ownerId, LocalDateTime now);

	@Query(nativeQuery = true, value = sqlFindByOwnerWithBookingDto,
			countQuery = "SELECT count(*) FROM ITEMS WHERE OWNER_ID = :ownerId AND :now=:now")
	Page<ItemWithBookingProjection> findByOwnerWithBookingDto(Integer ownerId, LocalDateTime now, Pageable page);

	List<Item> findAllByRequestId(Integer requestId);

	List<Item> findAllByRequestRequesterId(Integer requestId);

	List<Item> findAllByRequestRequesterIdNot(Integer userId);

	List<Item> findAllByRequestIn(List<Request> requestList);

}
