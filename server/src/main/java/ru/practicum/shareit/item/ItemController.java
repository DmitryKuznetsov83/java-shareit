package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;
	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public ItemRequestDto postItem(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(USER_HEADER) Integer ownerId) {
		return itemService.addItem(itemRequestDto, ownerId);
	}

	@PatchMapping("/{itemId}")
	public ItemRequestDto patchItem(@PathVariable Integer itemId,
	                                @RequestBody Map<String, String> patch,
	                                @RequestHeader(USER_HEADER) Integer userId) {
		return itemService.patchItem(itemId, userId, patch);
	}

	@GetMapping("/{itemId}")
	public ItemWithBookingAndCommentsResponseDto getItemById(@PathVariable Integer itemId, @RequestHeader(USER_HEADER) Integer userId) {
		return itemService.getItemById(itemId, userId);
	}

	@GetMapping
	public List<ItemWithBookingResponseDto> getItems(@RequestHeader(USER_HEADER) Integer ownerId,
													 @RequestParam(required = false) Integer from,
	                                                 @RequestParam(required = false) Integer size) {
		return itemService.getItems(ownerId, from, size);
	}

	@GetMapping("/search")
	public List<ItemRequestDto> searchItems(@RequestParam String text,
	                                        @RequestParam(required = false) Integer from,
	                                        @RequestParam(required = false) Integer size) {
		if (text.isBlank()) {
			return Collections.emptyList();
		}
		return itemService.search(text, from, size);
	}

	@PostMapping("/{itemId}/comment")
	public CommentResponseDto postComment(@PathVariable Integer itemId,
	                                      @RequestHeader(USER_HEADER) Integer authorId,
	                                      @RequestBody CommentRequestDto commentRequestDto) {
		return itemService.addComment(itemId, authorId, commentRequestDto);
	}

}