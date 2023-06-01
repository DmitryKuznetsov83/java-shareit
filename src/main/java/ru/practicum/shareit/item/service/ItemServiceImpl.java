package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
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
import ru.practicum.shareit.item.repository.ItemWithBookingProjection;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.DtoManager;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

	private final ItemJpaRepository itemRepository;
	private final UserService userService;
	private final BookingJpaRepository bookingJpaRepository;
	private final CommentJpaRepository commentJpaRepository;

	@Transactional
	@Override
	public ItemRequestDto addItem(ItemRequestDto itemRequestDto, Integer ownerId) {
		User owner = userService.getUserEntityById(ownerId);
		Item newItem = ItemMapper.mapToNewItem(itemRequestDto, owner);
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
		Item pachedItem = ItemMapper.mapToItem(patchedItemDto, user);
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
			List<Booking> nextAndLastBooking = bookingJpaRepository.findLastAndNextBooking(item.getId(), now);
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
	public List<ItemWithBookingResponseDto> getItems(Integer ownerId) {
		User owner = userService.getUserEntityById(ownerId);
		List<ItemWithBookingProjection> projectionList = itemRepository.findByOwnerWithBookingDto(ownerId, LocalDateTime.now());
		return projectionList
				.stream()
				.map(ItemWithBookingResponseDto::new)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public List<ItemRequestDto> search(String text) {
		return ItemMapper.mapToItemDtoList(itemRepository
				.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text));
	}

	@Transactional
	@Override
	public CommentResponseDto addComment(Integer itemId, Integer authorId, CommentRequestDto commentRequestDto) {
		Item item = getItemEntityById(itemId);
		User author = userService.getUserEntityById(authorId);
		List<Booking> bookings = bookingJpaRepository.findAllByItemAndBookerAndStatusAndEndIsLessThanOrderByStartDesc(item,
				author, BookingStatus.APPROVED, LocalDateTime.now());
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

}