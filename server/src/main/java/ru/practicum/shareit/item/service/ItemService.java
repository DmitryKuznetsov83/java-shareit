package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.Request;

import java.util.List;


public interface ItemService {

	ItemRequestDto addItem(ItemRequestDto itemRequestDto, Integer ownerId);

	ItemRequestDto patchItem(Integer itemId, Integer userId, ItemRequestDto patch);

	ItemWithBookingAndCommentsResponseDto getItemById(int itemId, int userId);

	Item getItemEntityById(int itemId);

	List<ItemWithBookingResponseDto> getItems(Integer ownerId, Integer from, Integer size);

	List<ItemRequestDto> search(String text, Integer from, Integer size);

	CommentResponseDto addComment(Integer itemId, Integer authorId, CommentRequestDto commentRequestDto);

	List<ItemRequestDto> getItemsByRequestId(Integer requestId);

	List<Item> getOwnRequestsItems(Integer requesterId);

	List<Item> getOtherRequestsItems(Integer requesterId);

	List<Item> getItemsByRequestList(List<Request> requestList);

}
