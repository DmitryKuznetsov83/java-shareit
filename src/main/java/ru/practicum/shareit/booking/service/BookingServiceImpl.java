package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.exception.IncorrectStatusChangeException;
import ru.practicum.shareit.booking.exception.ResourceNotAvailableException;
import ru.practicum.shareit.booking.exception.SelfBookingException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedChangeException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

	private final UserService userService;
	private final ItemService itemService;
	private final BookingJpaRepository bookingJpaRepository;

	@Transactional
	@Override
	public BookingDto addBooking(BookingCreationDto bookingCreationDto, Integer bookerId) {
		User booker = userService.getUserEntityById(bookerId);
		Item item = itemService.getItemEntityById(bookingCreationDto.getItemId());
		if (!item.getAvailable()) {
			throw new ResourceNotAvailableException("Item", item.getId());
		}
		if (booker.equals(item.getOwner())) {
			throw new SelfBookingException();
		}
		Booking newBooking = BookingMapper.mapToNewBooking(bookingCreationDto, booker, item);
		Booking savedBooking = bookingJpaRepository.save(newBooking);
		log.info("Create booking by booker {} for item {} with id {}", bookerId, item.getId(), savedBooking.getId());
		return BookingMapper.mapToBookingDto(savedBooking);
	}

	@Transactional
	@Override
	public BookingDto approveBooking(Integer bookingId, Integer userId, boolean approved) {
		Booking booking = getBookingById(bookingId);
		User user = userService.getUserEntityById(userId);
		if (!user.equals(booking.getItem().getOwner())) {
			throw new UnauthorizedChangeException("Booking", bookingId);
		}
		if (!booking.getStatus().equals(BookingStatus.WAITING)) {
			throw new IncorrectStatusChangeException();
		}
		booking.setStatus((approved ? BookingStatus.APPROVED : BookingStatus.REJECTED));
		Booking savedBooking = bookingJpaRepository.save(booking);
		log.info((approved ? "Approved" : "Rejected")  + " booking with id {}", bookingId);
		return BookingMapper.mapToBookingDto(savedBooking);
	}

	@Transactional(readOnly = true)
	@Override
	public BookingDto getBookingById(Integer bookingId, Integer userId) {
		Booking booking = getBookingById(bookingId);
		User user = userService.getUserEntityById(userId);
		if (!user.equals(booking.getBooker()) && !user.equals(booking.getItem().getOwner())) {
			throw new ResourceNotFoundException("Booking", bookingId);
		}
		return BookingMapper.mapToBookingDto(booking);
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookingDto> getBookersBookings(Integer bookerId, BookingState state) {
		User booker = userService.getUserEntityById(bookerId);
		return getBookings(bookerId, state, true);
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookingDto> getOwnersBookings(Integer ownerId, BookingState state) {
		User owner = userService.getUserEntityById(ownerId);
		return getBookings(ownerId, state, false);
	}

	// PRIVATE
	private Booking getBookingById(Integer bookingId) {
		return bookingJpaRepository.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
	}

	private List<BookingDto> getBookings(Integer userId, BookingState state, boolean booker) {

		LocalDateTime now = LocalDateTime.now();
		List<Predicate> predicateList = new ArrayList<>();

		if (booker) {
			predicateList.add(QBooking.booking.booker.id.eq(userId));
		} else {
			predicateList.add(QBooking.booking.item.owner.id.eq(userId));
		}

		switch (state) {
			case ALL:
				break;
			case CURRENT:
				predicateList.add(QBooking.booking.start.before(now).and(QBooking.booking.end.after(now)));
				break;
			case PAST:
				predicateList.add(QBooking.booking.end.before(now));
				break;
			case FUTURE:
				predicateList.add(QBooking.booking.start.after(now));
				break;
			case WAITING:
				predicateList.add(QBooking.booking.status.eq(BookingStatus.WAITING));
				break;
			case REJECTED:
				predicateList.add(QBooking.booking.status.eq(BookingStatus.REJECTED));
				break;
		}

		Predicate totalPredicate = ExpressionUtils.allOf(predicateList);
		Sort sort = Sort.by(Sort.Direction.DESC, "start");
		List<Booking> all = (List<Booking>)bookingJpaRepository.findAll(totalPredicate, sort);
		return BookingMapper.mapToBookingDtoList(all);

	}

}