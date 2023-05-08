package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;


import java.util.List;


public interface ItemService {

	Item addItem(Item item, Integer ownerId);

	Item updateItem(Item item, Integer itemId);

	void deleteItemById(Integer itemId);

	Item getItemById(int itemId);

	List<Item> getItems(Integer ownerId);

	List<Item> search(String text);

}
