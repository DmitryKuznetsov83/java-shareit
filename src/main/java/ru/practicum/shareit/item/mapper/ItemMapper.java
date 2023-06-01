package ru.practicum.shareit.item.mapper;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

	private static final ModelMapper modelMapper = new ModelMapper();

	public static Item mapToNewItem(ItemDto itemDto, User owner) {
		Item item = modelMapper.map(itemDto, Item.class);
		item.setOwner(owner);
		return item;
	}

	public static Item mapToItem(ItemDto itemDto, User owner) {
		Item item = modelMapper.map(itemDto, Item.class);
		item.setOwner(owner);
		return item;
	}

	public static ItemDto mapToItemDto(Item item) {
		return modelMapper.map(item, ItemDto.class);
	}

	public static ItemWithBookingAndCommentsDto mapToItemDetailedDto(Item item) {
		return modelMapper.map(item, ItemWithBookingAndCommentsDto.class);
	}

	public static List<ItemDto> mapToItemDtoList(List<Item> itemList) {
		return itemList.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
	}

}
