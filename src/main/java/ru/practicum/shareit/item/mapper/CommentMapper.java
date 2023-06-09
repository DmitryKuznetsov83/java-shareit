package ru.practicum.shareit.item.mapper;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto.ItemDetailedCommentDto;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

	private static final ModelMapper modelMapper = new ModelMapper();

	public static CommentResponseDto mapToCommentDto(Comment comment) {
		return modelMapper.map(comment, CommentResponseDto.class);
	}

	public static List<ItemDetailedCommentDto> mapToItemDetailedCommentDtoList(List<Comment> commentList) {
		return commentList
				.stream()
				.map(c -> new ItemDetailedCommentDto(c.getId(), c.getText(), c.getAuthor().getName(),
						c.getCreated()))
				.collect(Collectors.toList());
	}

}