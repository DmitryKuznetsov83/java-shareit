package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingJpaRepository;
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
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@InjectMocks
	BookingServiceImpl bookingService;

	@Mock
	private BookingJpaRepository bookingJpaRepository;

	private UserService userService;
	private ItemService itemService;

	private User owner;
	private User booker;
	private Item item;
	private BookingRequestDto bookingRequestDto;
	private Booking booking;

	@BeforeEach
	void setUp() {

		userService = Mockito.mock(UserService.class);
		itemService = Mockito.mock(ItemService.class);

		ReflectionTestUtils.setField(bookingService, "userService", userService);
		ReflectionTestUtils.setField(bookingService, "itemService", itemService);

		owner = User.builder()
				.id(1)
				.name("Owner")
				.email("owner@mail.ogr")
				.build();

		booker = User.builder()
				.id(2)
				.name("Booker")
				.email("booker@mail.ogr")
				.build();

		item = Item.builder()
				.id(1)
				.name("Item A")
				.description("Description A")
				.available(true)
				.owner(owner)
				.build();

		bookingRequestDto = BookingRequestDto.builder()
				.itemId(1)
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.build();

		booking = Booking.builder()
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.item(item)
				.booker(booker)
				.status(BookingStatus.WAITING)
				.build();

	}

	@Test
	void addBooking_whenItemIsAvailableAndBookerIsNotOwner_thenBookingReturned() {
		// when
		when(userService.getUserEntityById(2)).thenReturn(booker);
		when(itemService.getItemEntityById(anyInt())).thenReturn(item);
		when(bookingJpaRepository.save(any())).then(returnsFirstArg());
		BookingResponseDto bookingResponseDto = bookingService.addBooking(bookingRequestDto, 2);
		// then
		assertThat(bookingResponseDto.getStatus(), equalTo(BookingStatus.WAITING));
		verify(bookingJpaRepository).save(any());
	}

	@Test
	void addBooking_whenItemIsNotAvailable_thenResourceNotAvailableExceptionThrow() {
		// when
		when(userService.getUserEntityById(2)).thenReturn(booker);
		when(itemService.getItemEntityById(anyInt())).thenReturn(Item.builder().available(false).build());
		// then
		assertThrows(ResourceNotAvailableException.class, () -> bookingService.addBooking(bookingRequestDto, 2));
	}

	@Test
	void addBooking_whenBookerIsNotOwner_thenSelfBookingExceptionThrow() {
		// when
		when(userService.getUserEntityById(1)).thenReturn(owner);
		when(itemService.getItemEntityById(anyInt())).thenReturn(item);
		// then
		assertThrows(SelfBookingException.class, () -> bookingService.addBooking(bookingRequestDto, 1));
	}

	@Test
	void approveBooking_when_UserIsOwnerAndStatusIsWaiting_thenBookingApproved() {
		// when
		when(bookingJpaRepository.findById(1)).thenReturn(Optional.of(booking));
		when(userService.getUserEntityById(1)).thenReturn(owner);
		when(bookingJpaRepository.save(any())).then(returnsFirstArg());
		BookingResponseDto bookingResponseDto = bookingService.approveBooking(1, 1, true);
		// then
		assertThat(bookingResponseDto.getStatus(), equalTo(BookingStatus.APPROVED));
		verify(bookingJpaRepository).save(any());
	}

	@Test
	void approveBooking_when_UserIsNotOwner_thenUnauthorizedChangeExceptionThrown() {
		// when
		when(bookingJpaRepository.findById(1)).thenReturn(Optional.of(booking));
		when(userService.getUserEntityById(2)).thenReturn(booker);
		// then
		assertThrows(UnauthorizedChangeException.class, () -> bookingService.approveBooking(1, 2, true));
	}

	@Test
	void approveBooking_when_StatusIsNotWaiting_thenIncorrectStatusChangeExceptionThrown() {
		// given
		booking.setStatus(BookingStatus.APPROVED);
		// when
		when(bookingJpaRepository.findById(1)).thenReturn(Optional.of(booking));
		when(userService.getUserEntityById(1)).thenReturn(owner);
		// then
		assertThrows(IncorrectStatusChangeException.class, () -> bookingService.approveBooking(1, 1, true));
	}

	@Test
	void getBookingById_whenUserIsOwnerAndBookingFound_thenBookingReturned() {
		// when
		when(bookingJpaRepository.findById(1)).thenReturn(Optional.of(booking));
		when(userService.getUserEntityById(2)).thenReturn(booker);
		BookingResponseDto bookingResponseDto = bookingService.getBookingById(1, 2);
		// then
		assertNotNull(bookingResponseDto);
	}

	@Test
	void getBookingById_whenUserIsNeitherOwnerNorBooker_thenResourceNotFoundExceptionThrown() {
		// when
		when(userService.getUserEntityById(3)).thenReturn(new User());
		when(bookingJpaRepository.findById(1)).thenReturn(Optional.of(booking));
		// then
		assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(1, 3));
	}


	@Test
	void getBookersBookings_whenNoPagination_thenEmptyListReturned() {
		// then
		bookingService.getBookersBookings(2, BookingState.ALL, null, null);
		bookingService.getBookersBookings(2, BookingState.CURRENT, null, null);
		bookingService.getBookersBookings(2, BookingState.PAST, null, null);
		bookingService.getBookersBookings(2, BookingState.FUTURE, null, null);
		bookingService.getBookersBookings(2, BookingState.REJECTED, null, null);
		bookingService.getBookersBookings(2, BookingState.WAITING, null, null);
	}

	@Test
	void getBookersBookings_whenPagination_thenEmptyListReturned() {
		// when
		when(bookingJpaRepository.findAll(any(Predicate.class), any(PageRequest.class))).thenReturn(Page.empty());
		// then
		bookingService.getBookersBookings(2, BookingState.ALL, 0, 10);
	}


	@Test
	void getOwnersBookings_whenNoPagination_thenEmptyListReturned() {
		// then
		bookingService.getOwnersBookings(1, BookingState.ALL, null, null);
		bookingService.getOwnersBookings(1, BookingState.CURRENT, null, null);
		bookingService.getOwnersBookings(1, BookingState.PAST, null, null);
		bookingService.getOwnersBookings(1, BookingState.FUTURE, null, null);
		bookingService.getOwnersBookings(1, BookingState.REJECTED, null, null);
		bookingService.getOwnersBookings(1, BookingState.WAITING, null, null);
	}

	@Test
	void getOwnersBookings_whenPagination_thenEmptyListReturned() {
		// when
		when(bookingJpaRepository.findAll(any(Predicate.class), any(PageRequest.class))).thenReturn(Page.empty());
		// then
		bookingService.getOwnersBookings(1, BookingState.ALL, 0, 10);
	}


	@Test
	void getLastAndNextBookingOfItem() {
		// when
		bookingService.getLastAndNextBookingOfItem(1);
		// then
		verify(bookingJpaRepository).findLastAndNextBooking(anyInt(), any());
	}

	@Test
	void getFinishedBookingsByItemAndBooker() {
		// when
		bookingService.getFinishedBookingsByItemAndBooker(item, booker);
		// then
		verify(bookingJpaRepository).findAllByItemAndBookerAndStatusAndEndIsLessThanOrderByStartDesc(any(), any(), any(), any());
	}

	@Test
	void findAllByItemOwnerId() {
		// when
		bookingService.findAllByItemOwnerId(1);
		// then
		verify(bookingJpaRepository).findAllByItemOwnerId(any());
	}

	@Test
	void findAllByItems() {
		// when
		bookingService.findAllByItems(Collections.singletonList(item));
		// then
		verify(bookingJpaRepository).findAllByItemIn(any());
	}

}