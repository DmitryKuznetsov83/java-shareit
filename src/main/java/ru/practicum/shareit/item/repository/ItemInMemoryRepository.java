package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryRepository implements ItemRepository{

	private Integer itemId = 0;
	private final Map<Integer, Item> idItem = new HashMap<>();

	@Override
	public Item addItem(Item item) {
		Integer currentId = getItemId();
		item.setId(currentId);
		idItem.put(currentId, item);
		return item;
	}

	@Override
	public Item updateItem(Item item) {
		Integer itemId = item.getId();
		Item oldItem = idItem.get(itemId);
		if (oldItem == null) {
			return null;
		}
		idItem.put(item.getId(), item);
		return item;
	}

	@Override
	public Item getItem(Integer id) {
		return idItem.get(id);
	}

	@Override
	public List<Item> getItems(String searchText) {
		return idItem.values()
				.stream()
				.filter(item -> {
					String searchLowerCase = searchText.toLowerCase();
					String nameLowerCase = item.getName().toLowerCase();
					String descriptionLowerCase = item.getDescription().toLowerCase();
					return item.getAvailable()
							&& (nameLowerCase.contains(searchLowerCase)
							|| descriptionLowerCase.contains(searchLowerCase));
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<Item> getItems(Integer ownerId) {
		return idItem.values()
				.stream()
				.filter(item->ownerId.equals(item.getOwner().getId()))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteItemById(Integer itemId) {
		Item item = idItem.get(itemId);
		if (item == null) {
			return;
		}
		idItem.remove(itemId);
	}


	// PRIVATE
	private Integer getItemId() {
		return ++itemId;
	}

}
