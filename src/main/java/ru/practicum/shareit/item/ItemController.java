package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aop.OnCreate;
import ru.practicum.shareit.item.dto.comment.CommentCreationDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

	private final ItemService itemService;
	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	@Validated(OnCreate.class)
	public ItemDto postItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(USER_HEADER) Integer ownerId) {
		return itemService.addItem(itemDto, ownerId);
	}

	@PatchMapping("/{itemId}")
	public ItemDto patchItem(@PathVariable Integer itemId,
	                         @RequestBody @NotNull Map<String, String> patch,
	                         @RequestHeader(USER_HEADER) Integer userId) {
		return itemService.patchItem(itemId, userId, patch);
	}

	@GetMapping("/{itemId}")
	public ItemWithBookingAndCommentsDto getItemById(@PathVariable Integer itemId, @RequestHeader(USER_HEADER) Integer userId) {
		return itemService.getItemById(itemId, userId);
	}

	@GetMapping
	public List<ItemWithBookingDto> getItems(@RequestHeader(USER_HEADER) Integer ownerId) {
		return itemService.getItems(ownerId);
	}

	@GetMapping("/search")
	public List<ItemDto> searchItems(@RequestParam String text) {
		if (text.isBlank()) {
			return Collections.emptyList();
		}
		return itemService.search(text);
	}

	@PostMapping("/{itemId}/comment")
	@Validated
	public CommentDto postComment(@PathVariable Integer itemId,
	                              @RequestHeader(USER_HEADER) Integer authorId,
	                              @Valid @RequestBody CommentCreationDto commentCreationDto) {
		return itemService.addComment(itemId, authorId, commentCreationDto);
	}

}