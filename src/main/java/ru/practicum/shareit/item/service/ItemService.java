package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.Map;


public interface ItemService {

	ItemRequestDto addItem(ItemRequestDto itemRequestDto, Integer ownerId);

	ItemRequestDto patchItem(Integer itemId, Integer userId, Map<String, String> patch);

	ItemWithBookingAndCommentsResponseDto getItemById(int itemId, int userId);

	Item getItemEntityById(int itemId);

	List<ItemWithBookingResponseDto> getItems(Integer ownerId);

	List<ItemRequestDto> search(String text);

	CommentResponseDto addComment(Integer itemId, Integer authorId, CommentRequestDto commentRequestDto);

}
