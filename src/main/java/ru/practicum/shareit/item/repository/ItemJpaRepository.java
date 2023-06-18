package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.Request;

import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Integer> {

	List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String nameSearch,
	                                                                                             String descSearch);

	List<Item> findAllByOwnerId(Integer ownerId);

	Page<Item> findAllByOwnerId(Integer ownerId, Pageable page);

	List<Item> findAllByRequestId(Integer requestId);

	List<Item> findAllByRequestRequesterId(Integer requestId);

	List<Item> findAllByRequestRequesterIdNot(Integer userId);

	List<Item> findAllByRequestIn(List<Request> requestList);

}
