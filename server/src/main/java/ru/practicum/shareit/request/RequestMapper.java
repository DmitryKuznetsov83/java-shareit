package ru.practicum.shareit.request;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestMapper {

	private static final ModelMapper modelMapper = new ModelMapper();

	public static Request mapToNewRequest(RequestRequestDto itemRequestDto, User requester) {
		Request request = modelMapper.map(itemRequestDto, Request.class);
		request.setRequester(requester);
		request.setCreated(LocalDateTime.now());
		return request;
	}

	public static RequestResponseDto mapToRequestDto(Request request, List<ItemRequestDto> itemList) {
		RequestResponseDto requestResponseDto = modelMapper.map(request, RequestResponseDto.class);
		requestResponseDto.setItems(itemList);
		return requestResponseDto;
	}

	public static List<RequestResponseDto> mapToRequestDtoList(List<Request> requestList, Map<Request, List<Item>> itemMap) {
		return requestList.stream().map(r -> {
			List<ItemRequestDto> itemRequestDtoList = ItemMapper.mapToItemDtoList(itemMap.getOrDefault(r, Collections.EMPTY_LIST));
			RequestResponseDto requestResponseDto = RequestMapper.mapToRequestDto(r, itemRequestDtoList);
			return requestResponseDto;
		}).collect(Collectors.toList());
	}

}