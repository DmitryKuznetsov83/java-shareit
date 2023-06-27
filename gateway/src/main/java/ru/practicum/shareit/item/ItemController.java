package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aop.OnCreate;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

	private final ItemClient itemClient;

	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	@Validated(OnCreate.class)
	public ResponseEntity<Object> postItem(@Valid @RequestBody ItemRequestDto itemRequestDto, @RequestHeader(USER_HEADER) Integer ownerId) {
		log.info("Create item {}, userId={}", itemRequestDto, ownerId);
		return itemClient.postItem(itemRequestDto, ownerId);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> patchItem(@PathVariable Integer itemId,
	                                @RequestBody ItemRequestDto patch,
	                                @RequestHeader(USER_HEADER) Integer userId) {
		log.info("Patch item with id {}, userId={}", itemId, userId);
		return itemClient.patchItem(itemId, userId, patch);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getItemById(@PathVariable Integer itemId, @RequestHeader(USER_HEADER) Integer userId) {
		log.info("Get item by id {}, userId={}", itemId, userId);
		return itemClient.getItemById(itemId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getItems(@RequestHeader(USER_HEADER) Integer ownerId,
	                                                 @RequestParam(required = false) @PositiveOrZero Integer from,
	                                                 @RequestParam(required = false) @Positive Integer size) {
		log.info("Get items, ownerId={}, from={}, size={}", ownerId, from, size);
		return itemClient.getItems(ownerId, from, size);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> searchItems(@RequestParam String text,
	                                        @RequestParam(required = false) @PositiveOrZero Integer from,
	                                        @RequestParam(required = false) @Positive Integer size) {
		if (text.isBlank()) {
			return ResponseEntity.of(Optional.of(Collections.emptyList()));
		}
		log.info("Search items by text {}, from={}, size={}", text, from, size);
		return itemClient.search(text, from, size);
	}

	@PostMapping("/{itemId}/comment")
	@Validated
	public ResponseEntity<Object> postComment(@PathVariable Integer itemId,
	                                      @RequestHeader(USER_HEADER) Integer authorId,
	                                      @Valid @RequestBody CommentRequestDto commentRequestDto) {
		log.info("Post comment for item {}, authorId={}", itemId, authorId);
		return itemClient.addComment(itemId, authorId, commentRequestDto);
	}

}
