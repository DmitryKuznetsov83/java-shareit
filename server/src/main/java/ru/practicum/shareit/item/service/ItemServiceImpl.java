package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedChangeException;
import ru.practicum.shareit.exception.UnauthorizedCommentException;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DtoManager;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

	private final ItemJpaRepository itemRepository;
	private final CommentJpaRepository commentJpaRepository;

	private UserService userService;
	private RequestService requestService;
	private BookingService bookingService;

	@Transactional
	@Override
	public ItemRequestDto addItem(ItemRequestDto itemRequestDto, Integer ownerId) {
		User owner = userService.getUserEntityById(ownerId);
		Request request = getRequestFromItemDto(itemRequestDto);
		Item newItem = ItemMapper.mapToNewItem(itemRequestDto, owner, request);
		Item savedItem = itemRepository.save(newItem);
		log.info("Create item with name {} id {}", savedItem.getName(), savedItem.getId());
		return ItemMapper.mapToItemDto(savedItem);
	}

	@Transactional
	@Override
	public ItemRequestDto patchItem(Integer itemId, Integer userId, Map<String, String> patch) {
		Item item = getItemEntityById(itemId);
		User user = userService.getUserEntityById(userId);
		if (!item.getOwner().equals(user)) {
			throw new UnauthorizedChangeException("Item", itemId);
		}
		ItemRequestDto itemRequestDto = ItemMapper.mapToItemDto(item);
		ItemRequestDto patchedItemDto = DtoManager.patch(itemRequestDto, patch);
		DtoManager.validate(patchedItemDto);
		Request request = getRequestFromItemDto(patchedItemDto);
		Item pachedItem = ItemMapper.mapToItem(patchedItemDto, user, request);
		Item savedItem = itemRepository.save(pachedItem);
		log.info("Patch item with id {}", itemId);
		return ItemMapper.mapToItemDto(savedItem);
	}

	@Transactional(readOnly = true)
	@Override
	public ItemWithBookingAndCommentsResponseDto getItemById(int itemId, int userId) {
		Item item = getItemEntityById(itemId);
		List<Comment> comments = commentJpaRepository.findAllByItemOrderByCreatedDesc(item);
		ItemWithBookingAndCommentsResponseDto itemWithBookingAndCommentsResponseDto = ItemMapper.mapToItemDetailedDto(item);
		List<ItemWithBookingAndCommentsResponseDto.ItemDetailedCommentDto> commentsDto = CommentMapper
				.mapToItemDetailedCommentDtoList(comments);
		itemWithBookingAndCommentsResponseDto.setComments(commentsDto);
		if (item.getOwner().getId().equals(userId)) {
			LocalDateTime now = LocalDateTime.now();
			List<Booking> nextAndLastBooking = bookingService.getLastAndNextBookingOfItem(item.getId());
					nextAndLastBooking.forEach(b -> {
				if (b.getStart().isBefore(now)) {
					itemWithBookingAndCommentsResponseDto.setLastBooking(BookingMapper.mapToBookingShortDto(b));
				} else if (b.getStart().isAfter(now)) {
					itemWithBookingAndCommentsResponseDto.setNextBooking(BookingMapper.mapToBookingShortDto(b));
				}
			});
		}
		return itemWithBookingAndCommentsResponseDto;
	}

	@Transactional(readOnly = true)
	@Override
	public Item getItemEntityById(int itemId) {
		return itemRepository.findById(itemId).orElseThrow(() -> new ResourceNotFoundException("Item", itemId));
	}

	@Transactional(readOnly = true)
	@Override
	public List<ItemWithBookingResponseDto> getItems(Integer ownerId, Integer from, Integer size) {
		User owner = userService.getUserEntityById(ownerId);

		List<Item> itemList;
		List<Booking> bookingList;
		if (from == null) {
			itemList = itemRepository.findAllByOwnerIdOrderById(ownerId);
			bookingList = bookingService.findAllByItemOwnerId(ownerId);
		} else {
			PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id"));
			itemList = itemRepository.findAllByOwnerIdOrderById(ownerId, pageRequest).getContent();
			bookingList = bookingService.findAllByItems(itemList);
		}

		Comparator<Booking> bookingComparator = Comparator.comparing(Booking::getStart);
		LocalDateTime now = LocalDateTime.now();

		Map<Item, List<Booking>> bookingMap = bookingList
				.stream()
				.collect(Collectors.groupingBy(Booking::getItem, Collectors.toCollection(ArrayList::new)));

		List<ItemWithBookingResponseDto> itemsDto = itemList.stream().map(i -> {
			List<Booking> bookings = bookingMap.getOrDefault(i, Collections.emptyList());
			Booking lastBooking = bookings.stream().filter(booking -> booking.getStart().isBefore(now)).max(bookingComparator).orElse(null);
			Booking nextBooking = bookings.stream().filter(booking -> booking.getStart().isAfter(now)).min(bookingComparator).orElse(null);

			ItemWithBookingResponseDto itemWithBookingResponseDto = ItemMapper.mapToItemWithBookingResponseDto(i);
			BookingShortResponseDto lastBookingShortResponseDto = (lastBooking != null ? BookingMapper.mapToBookingShortDto(lastBooking) : null);
			BookingShortResponseDto nextBookingShortResponseDto = (nextBooking != null ? BookingMapper.mapToBookingShortDto(nextBooking) : null);

			itemWithBookingResponseDto.setLastBooking(lastBookingShortResponseDto);
			itemWithBookingResponseDto.setNextBooking(nextBookingShortResponseDto);

			return itemWithBookingResponseDto;
		}).collect(Collectors.toList());

		return itemsDto;

	}

	@Transactional(readOnly = true)
	@Override
	public List<ItemRequestDto> search(String text, Integer from, Integer size) {
		return ItemMapper.mapToItemDtoList(itemRepository
				.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text));
	}

	@Transactional
	@Override
	public CommentResponseDto addComment(Integer itemId, Integer authorId, CommentRequestDto commentRequestDto) {
		Item item = getItemEntityById(itemId);
		User author = userService.getUserEntityById(authorId);
		List<Booking> bookings = bookingService.getFinishedBookingsByItemAndBooker(item, author);
		if (bookings.isEmpty()) {
			throw new UnauthorizedCommentException("Comment for item " + itemId + " is not legal");
		}
		Comment newComment = Comment.builder()
				.text(commentRequestDto.getText())
				.item(item)
				.author(author)
				.build();
		Comment savedComment = commentJpaRepository.save(newComment);
		log.info("Create comment with id {} for item with id {}", savedComment.getId(), itemId);
		return CommentMapper.mapToCommentDto(savedComment);
	}

	@Override
	public List<ItemRequestDto> getItemsByRequestId(Integer requestId) {
		return ItemMapper.mapToItemDtoList(itemRepository.findAllByRequestId(requestId));
	}

	@Override
	public List<Item> getOwnRequestsItems(Integer requesterId) {
		return itemRepository.findAllByRequestRequesterId(requesterId);
	}

	@Override
	public List<Item> getOtherRequestsItems(Integer requesterId) {
		return itemRepository.findAllByRequestRequesterIdNot(requesterId);
	}

	@Override
	public List<Item> getItemsByRequestList(List<Request> requestList) {
		return itemRepository.findAllByRequestIn(requestList);
	}

	// PRIVATE
	private Request getRequestFromItemDto(ItemRequestDto itemRequestDto) {
		Request request = null;
		Integer requestId = itemRequestDto.getRequestId();
		if (requestId != null) {
			request = requestService.getRequestEntityById(requestId);
		}
		return request;
	}

	// DEPENDENCY INJECTION
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	@Autowired
	public void setBookingService(BookingService bookingService) {
		this.bookingService = bookingService;
	}

}