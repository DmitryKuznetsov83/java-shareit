package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aop.ErrorResponse;
import ru.practicum.shareit.exception.IncorrectIdException;
import ru.practicum.shareit.exception.UnauthorizedItemChangeException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.DtoManager;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;
	private final ModelMapper modelMapper = new ModelMapper();

	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public ItemDto postItem(@Valid @RequestBody ItemDto itemDto,
	                        @RequestHeader(USER_HEADER) Integer ownerId) {
		if (itemDto.getId() != null) {
			throw new IncorrectIdException("Item ID must be empty");
		}
		Item item = convertItemToEntity(itemDto);
		Item itemSaved = itemService.addItem(item, ownerId);
		return convertItemToDto(itemSaved);
	}

	@PatchMapping("/{itemId}")
	public ItemDto patchItem(@PathVariable Integer itemId,
	                         @RequestBody @NotNull Map<String, String> body,
	                         @RequestHeader(USER_HEADER) Integer ownerId) {

		// get old item
		ItemDto itemDto = getItemById(itemId);

		// patch fields
		ItemDto patchedItemDto = DtoManager.patch(itemDto, body);

		// validate dto
		DtoManager.validate(patchedItemDto);

		// update
		Item item = convertItemToEntity(patchedItemDto);
		Item patchedItem = itemService.updateItem(item, ownerId);
		return convertItemToDto(patchedItem);
	}

	@DeleteMapping("/{itemId}")
	public void deleteItem(@PathVariable Integer itemId) {
		itemService.deleteItemById(itemId);
	}

	@GetMapping("/{itemId}")
	public ItemDto getItemById(@PathVariable Integer itemId) {
		return convertItemToDto(itemService.getItemById(itemId));
	}

	@GetMapping
	public List<ItemDto> getItems(@RequestHeader(USER_HEADER) Integer ownerId) {
		return itemService
				.getItems(ownerId)
				.stream()
				.map(this::convertItemToDto)
				.collect(Collectors.toList());
	}

	@GetMapping("/search")
	public List<ItemDto> searchItems(@RequestParam String text) {
		if (text.isBlank()) {
			return Collections.emptyList();
		}
		return itemService.search(text)
				.stream()
				.map(this::convertItemToDto)
				.collect(Collectors.toList());
	}


	// ERRORS HANDLING
	@ExceptionHandler({MissingRequestHeaderException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST,"Header X-Sharer-User-Id is empty", e.getMessage());
	}

	@ExceptionHandler({UnauthorizedItemChangeException.class})
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleMissingRequestHeaderException(final UnauthorizedItemChangeException e) {
		return new ErrorResponse(HttpStatus.FORBIDDEN,"Forbidden operation", e.getMessage());
	}


	// PRIVATE
	private ItemDto convertItemToDto(Item item) {
		return modelMapper.map(item, ItemDto.class);
	}

	private Item convertItemToEntity(ItemDto itemDto) {
		return modelMapper.map(itemDto, Item.class);
	}

}