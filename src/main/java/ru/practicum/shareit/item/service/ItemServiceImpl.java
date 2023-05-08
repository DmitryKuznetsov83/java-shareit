package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedItemChangeException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final UserService userService;

	@Override
	public Item addItem(Item item, Integer ownerId) {
		User owner = userService.getUserById(ownerId);
		item.setOwner(owner);
		Item savedItem = itemRepository.addItem(item);
		return savedItem;
	}

	@Override
	public Item updateItem(Item item, Integer ownerId) {
		Item oldItem = itemRepository.getItem(item.getId());
		User owner = userService.getUserById(ownerId);
		if (oldItem.getOwner().equals(owner)) {
			item.setOwner(owner);
		} else {
			throw new UnauthorizedItemChangeException("You can't change item with id " + item.getId());
		}
		Item savedItem = itemRepository.updateItem(item);
		return savedItem;
	}

	@Override
	public Item deleteItemById(Integer itemId) {
		// checking
		Item item = itemRepository.getItem(itemId);
		if (item == null) {
			throw new ResourceNotFoundException("Item", itemId);
		}

		// delete
		itemRepository.deleteItemById(itemId);
		return item;
	}

	@Override
	public Item getItemById(int itemId) {
		// checking
		Item item = itemRepository.getItem(itemId);
		if (item == null) {
			throw new ResourceNotFoundException("Item", itemId);
		}

		// get
		return item;
	}

	@Override
	public List<Item> getItems(Integer ownerId) {
		return itemRepository.getItems(ownerId);
	}

	@Override
	public List<Item> search(String text) {
		return itemRepository.getItems(text);
	}

}
