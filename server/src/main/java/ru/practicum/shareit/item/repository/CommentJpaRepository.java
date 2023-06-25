package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Integer> {

	List<Comment> findAllByItemOrderByCreatedDesc(Item item);

}
