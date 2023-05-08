package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

	// ITEMS - CRUD
	Item addItem(Item item);

	Item updateItem(Item item);

	Item getItem(Integer id);

	List<Item> getItems(String searchText);

	List<Item> getItems(Integer ownerId);

	void deleteItemById(Integer ItemId);

}
