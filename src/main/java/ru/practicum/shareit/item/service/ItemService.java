package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.comment.CommentCreationDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.Map;


public interface ItemService {

	ItemDto addItem(ItemDto itemDto, Integer ownerId);

	ItemDto patchItem(Integer itemId, Integer userId, Map<String, String> patch);

	ItemWithBookingAndCommentsDto getItemById(int itemId, int userId);

	Item getItemEntityById(int itemId);

	List<ItemWithBookingDto> getItems(Integer ownerId);

	List<ItemDto> search(String text);

	CommentDto addComment(Integer itemId, Integer authorId, CommentCreationDto commentCreationDto);

}
