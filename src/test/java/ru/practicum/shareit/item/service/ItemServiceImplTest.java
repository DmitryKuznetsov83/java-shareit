package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedCommentException;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

	@InjectMocks
	private ItemServiceImpl itemService;

	@Mock
	private ItemJpaRepository itemRepository;
	@Mock
	private CommentJpaRepository commentJpaRepository;

	private UserService userService;
	private RequestService requestService;
	private BookingService bookingService;

	private User owner;
	private User requester;
	private Request request;
	private ItemRequestDto itemRequestDto;
	private Item item;
	private Comment comment;
	private Booking lastBooking;
	private Booking nextBooking;
	private CommentRequestDto commentRequestDto;

	@BeforeEach
	void setUp() {

		userService = Mockito.mock(UserService.class);
		requestService = Mockito.mock(RequestService.class);
		bookingService = Mockito.mock(BookingService.class);

		ReflectionTestUtils.setField(itemService, "userService", userService);
		ReflectionTestUtils.setField(itemService, "requestService", requestService);
		ReflectionTestUtils.setField(itemService, "bookingService", bookingService);

		// id 1
		requester = User.builder()
				.id(1)
				.name("Requester")
				.email("Requester@mail.ogr")
				.build();

		// id 2
		owner = User.builder()
				.id(2)
				.name("Owner")
				.email("owner@mail.ogr")
				.build();

		itemRequestDto = ItemRequestDto.builder()
				.name("Item A")
				.description("")
				.available(true)
				.requestId(1)
				.build();

		request = Request.builder()
				.description("Item A is needed")
				.requester(requester)
				.created(LocalDateTime.now())
				.build();

		item = Item.builder()
				.name("Item A")
				.description("Description A")
				.available(true)
				.owner(owner)
				.request(request)
				.build();

		comment = Comment.builder()
				.text("Comment for item A")
				.item(item)
				.author(requester)
				.created(LocalDateTime.now())
				.build();

		lastBooking = Booking.builder()
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.item(item)
				.booker(requester)
				.build();

		nextBooking = Booking.builder()
				.start(LocalDateTime.now().plusDays(1))
				.end(LocalDateTime.now().plusDays(2))
				.item(item)
				.booker(requester)
				.build();

		commentRequestDto = CommentRequestDto.builder()
				.text("Comment")
				.build();

	}

	@Test
	void addItem_whenOwnerExistsAndRequestExists_thenItemAdded() {
		// when
		when(userService.getUserEntityById(2)).thenReturn(owner);
		when(requestService.getRequestEntityById(any())).thenReturn(request);
		when(itemRepository.save(any())).then(returnsFirstArg());
		ItemRequestDto addedItemRequestDto = itemService.addItem(itemRequestDto, 2);
		// then
		assertThat(addedItemRequestDto.getName(), equalTo(itemRequestDto.getName()));
		assertThat(item.getOwner(), equalTo(owner));
		verify(itemRepository).save(any());
	}

	@Test
	void addItem_whenOwnerNotFound_thenItemNotAdded() {
		// when
		doThrow(ResourceNotFoundException.class).when(userService).getUserEntityById(2);
		// then
		assertThrows(ResourceNotFoundException.class, () -> itemService.addItem(itemRequestDto, 2));
		verify(itemRepository, never()).save(item);
	}


	@Test
	void patchItem_whenUserIsOwner_thenPatchedItemReturned() {
		// given
		Map<String, String> patch = new HashMap<>();
		patch.put("name", "patched name");
		// when
		when(itemRepository.findById(any())).thenReturn(Optional.of(item));
		when(userService.getUserEntityById(anyInt())).thenReturn(owner);
		when(requestService.getRequestEntityById(anyInt())).thenReturn(request);
		when(itemRepository.save(any())).then(returnsFirstArg());
		ItemRequestDto patchedItemRequest = itemService.patchItem(1, 1, patch);
		// then
		assertThat(patchedItemRequest.getName(), equalTo(patch.get("name")));
		assertThat(patchedItemRequest.getDescription(), equalTo(item.getDescription()));
		verify(itemRepository).save(any());
	}

	@Test
	void getItemById_whenItemFoundAndUserIsOwner_thenItemWithBookingsReturned() {
		// when
		when(itemRepository.findById(1)).thenReturn(Optional.of(item));
		when(commentJpaRepository.findAllByItemOrderByCreatedDesc(item)).thenReturn(Collections.singletonList(comment));
		when(bookingService.getLastAndNextBookingOfItem(any())).thenReturn(Arrays.asList(lastBooking, nextBooking));
		ItemWithBookingAndCommentsResponseDto itemDto = itemService.getItemById(1, 2);
		// then
		assertThat(itemDto.getName(), equalTo(item.getName()));
		assertThat(itemDto.getComments().size(), equalTo(1));
		assertThat(itemDto.getComments().get(0).getText(), equalTo(comment.getText()));
		assertNotNull(itemDto.getLastBooking());
		assertNotNull(itemDto.getNextBooking());
	}

	@Test
	void getItemById_whenItemFoundAndUserIsNotOwner_thenItemWithoutBookingsReturned() {
		// when
		when(itemRepository.findById(1)).thenReturn(Optional.of(item));
		when(commentJpaRepository.findAllByItemOrderByCreatedDesc(item)).thenReturn(Collections.singletonList(comment));
		ItemWithBookingAndCommentsResponseDto itemDto = itemService.getItemById(1, 3);
		// then
		assertThat(itemDto.getName(), equalTo(item.getName()));
		assertThat(itemDto.getComments().size(), equalTo(1));
		assertThat(itemDto.getComments().get(0).getText(), equalTo(comment.getText()));
		assertNull(itemDto.getLastBooking());
		assertNull(itemDto.getNextBooking());
	}

	@Test
	void getItemById_whenItemNotFound_thenResourceNotFoundExceptionThrown() {
		// when
		doThrow(ResourceNotFoundException.class).when(itemRepository).findById(any());
		// then
		assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(1, 1));
	}


	@Test
	void getItems_whenOwnItemsFoundAndNoPagination_thenListReturned() {
		// when
		when(itemRepository.findAllByOwnerId(anyInt())).thenReturn(Collections.singletonList(item));
		when(bookingService.findAllByItemOwnerId(anyInt())).thenReturn(Arrays.asList(lastBooking, nextBooking));
		List<ItemWithBookingResponseDto> items = itemService.getItems(2, null, null);
		// then
		assertThat(items.size(), equalTo(1));
	}

	@Test
	void getItems_whenOwnItemsFoundAndPagination_thenListReturned() {
		// when
		when(itemRepository.findAllByOwnerId(anyInt(), any())).thenReturn(new PageImpl<>(Collections.singletonList(item)));
		when(bookingService.findAllByItems(anyList())).thenReturn(Arrays.asList(lastBooking, nextBooking));
		List<ItemWithBookingResponseDto> items = itemService.getItems(2, 0, 10);
		// then
		assertThat(items.size(), equalTo(1));
	}


	@Test
	void search_whenFound_thenListReturned() {
		// when
		when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any())).thenReturn(Collections.singletonList(item));
		List<ItemRequestDto> items = itemService.search("text", null, null);
		// then
		assertThat(items.size(), equalTo(1));
	}

	@Test
	void addComment_whenFinishedBookingExist_thenCommentAdded() {
		// when
		when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
		when(userService.getUserEntityById(anyInt())).thenReturn(requester);
		when(bookingService.getFinishedBookingsByItemAndBooker(any(), any())).thenReturn(Collections.singletonList(lastBooking));
		when(commentJpaRepository.save(any())).then(returnsFirstArg());
		CommentResponseDto commentResponseDto = itemService.addComment(1, 1, commentRequestDto);
		// then
		assertThat(commentResponseDto.getText(), equalTo("Comment"));
		assertThat(commentResponseDto.getAuthorName(), equalTo(requester.getName()));
	}

	@Test
	void addComment_whenNoFinishedBooking_thenUnauthorizedCommentExceptionThrown() {
		// when
		when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
		when(userService.getUserEntityById(anyInt())).thenReturn(requester);
		when(bookingService.getFinishedBookingsByItemAndBooker(any(), any())).thenReturn(Collections.emptyList());
		// then
		assertThrows(UnauthorizedCommentException.class, () -> itemService.addComment(1, 1, commentRequestDto));
		verify(commentJpaRepository, never()).save(any());
	}

	@Test
	void getItemsByRequestId() {
		// when
		itemService.getItemsByRequestId(1);
		// then
		verify(itemRepository).findAllByRequestId(anyInt());
	}

	@Test
	void getOwnRequestsItems() {
		// when
		itemService.getOwnRequestsItems(1);
		// then
		verify(itemRepository).findAllByRequestRequesterId(anyInt());
	}

	@Test
	void getOtherRequestsItems() {
		// when
		itemService.getOtherRequestsItems(1);
		// then
		verify(itemRepository).findAllByRequestRequesterIdNot(anyInt());
	}

	@Test
	void getItemsByRequestList() {
		// when
		List<Request> requestList = Collections.singletonList(request);
		itemService.getItemsByRequestList(requestList);
		// then
		verify(itemRepository).findAllByRequestIn(requestList);
	}
}