package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
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

	private final BookingJpaRepository bookingJpaRepository;

	private UserService userService;
	private ItemService itemService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	@Transactional
	@Override
	public BookingResponseDto addBooking(BookingRequestDto bookingRequestDto, Integer bookerId) {
		User booker = userService.getUserEntityById(bookerId);
		Item item = itemService.getItemEntityById(bookingRequestDto.getItemId());
		if (!item.getAvailable()) {
			throw new ResourceNotAvailableException("Item", item.getId());
		}
		if (booker.equals(item.getOwner())) {
			throw new SelfBookingException();
		}
		Booking newBooking = BookingMapper.mapToNewBooking(bookingRequestDto, booker, item);
		Booking savedBooking = bookingJpaRepository.save(newBooking);
		log.info("Create booking by booker {} for item {} with id {}", bookerId, item.getId(), savedBooking.getId());
		return BookingMapper.mapToBookingDto(savedBooking);
	}

	@Transactional
	@Override
	public BookingResponseDto approveBooking(Integer bookingId, Integer userId, boolean approved) {
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
	public BookingResponseDto getBookingById(Integer bookingId, Integer userId) {
		Booking booking = getBookingById(bookingId);
		User user = userService.getUserEntityById(userId);
		if (!user.equals(booking.getBooker()) && !user.equals(booking.getItem().getOwner())) {
			throw new ResourceNotFoundException("Booking", bookingId);
		}
		return BookingMapper.mapToBookingDto(booking);
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookingResponseDto> getBookersBookings(Integer bookerId, BookingState state, Integer from, Integer size) {
		User booker = userService.getUserEntityById(bookerId);
		Predicate predicate = getPredicate(bookerId, state, true);
		return getBookings(predicate, from, size);
	}

	@Transactional(readOnly = true)
	@Override
	public List<BookingResponseDto> getOwnersBookings(Integer ownerId, BookingState state, Integer from, Integer size) {
		User owner = userService.getUserEntityById(ownerId);
		Predicate predicate = getPredicate(ownerId, state, false);
		return getBookings(predicate, from, size);
	}

	@Override
	public List<Booking> getLastAndNextBookingOfItem(Integer itemId) {
		return bookingJpaRepository.findLastAndNextBooking(itemId, LocalDateTime.now());
	}

	@Override
	public List<Booking> getFinishedBookingsByItemAndBooker(Item item, User booker) {
		return bookingJpaRepository.findAllByItemAndBookerAndStatusAndEndIsLessThanOrderByStartDesc(item, booker,
				BookingStatus.APPROVED, LocalDateTime.now());
	}

	@Override
	public List<Booking> findAllByItemOwnerId(Integer ownerId) {
		return bookingJpaRepository.findAllByItemOwnerId(ownerId);
	}

	@Override
	public List<Booking> findAllByItems(List<Item> itemList) {
		return bookingJpaRepository.findAllByItemIn(itemList);
	}


	// PRIVATE
	private Booking getBookingById(Integer bookingId) {
		return bookingJpaRepository.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
	}

	private Predicate getPredicate(Integer userId, BookingState state, boolean booker) {

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

		return ExpressionUtils.allOf(predicateList);

	}

	private List<BookingResponseDto> getBookings(Predicate predicate, Integer from, Integer size) {
		Sort sort = Sort.by(Sort.Direction.DESC, "start");
		if (from == null) {
			return getBookings(predicate, sort);
		} else {
			PageRequest pageRequest = PageRequest.of(from / size, size, sort);
			return getBookings(predicate, pageRequest);
		}
	}

	private List<BookingResponseDto> getBookings(Predicate predicate, Sort sort) {
		List<Booking> bookingList = (List<Booking>)bookingJpaRepository.findAll(predicate, sort);
		return BookingMapper.mapToBookingDtoList(bookingList);
	}

	private List<BookingResponseDto> getBookings(Predicate predicate, PageRequest pageRequest) {
		List<Booking> bookingList = bookingJpaRepository.findAll(predicate, pageRequest).getContent();;
		return BookingMapper.mapToBookingDtoList(bookingList);
	}

}