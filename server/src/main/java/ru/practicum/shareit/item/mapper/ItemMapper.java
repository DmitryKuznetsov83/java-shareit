package ru.practicum.shareit.item.mapper;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;


import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

	private static final ModelMapper modelMapper = new ModelMapper();

	public static Item mapToNewItem(ItemRequestDto itemRequestDto, User owner, Request request) {
		return Item.builder()
				.name(itemRequestDto.getName())
				.description(itemRequestDto.getDescription())
				.available(itemRequestDto.getAvailable())
				.owner(owner)
				.request(request)
				.build();
	}

	public static Item mapToItem(ItemRequestDto itemRequestDto, User owner, Request request) {
		return Item.builder()
				.id(itemRequestDto.getId())
				.name(itemRequestDto.getName())
				.description(itemRequestDto.getDescription())
				.available(itemRequestDto.getAvailable())
				.owner(owner)
				.request(request)
				.build();
	}

	public static ItemRequestDto mapToItemDto(Item item) {
		return modelMapper.map(item, ItemRequestDto.class);
	}

	public static ItemWithBookingAndCommentsResponseDto mapToItemDetailedDto(Item item) {
		return modelMapper.map(item, ItemWithBookingAndCommentsResponseDto.class);
	}

	public static List<ItemRequestDto> mapToItemDtoList(List<Item> itemList) {
		return itemList.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
	}

	public static ItemWithBookingResponseDto mapToItemWithBookingResponseDto(Item item) {
		return  modelMapper.map(item, ItemWithBookingResponseDto.class);
	}

}
