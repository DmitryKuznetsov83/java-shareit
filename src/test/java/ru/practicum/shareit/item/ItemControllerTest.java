package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UnauthorizedChangeException;
import ru.practicum.shareit.exception.UnauthorizedCommentException;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingAndCommentsResponseDto;
import ru.practicum.shareit.item.dto.item.ItemWithBookingResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

	@MockBean
	ItemService itemService;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private MockMvc mvc;

	@Test
	@SneakyThrows
	void postItem_whenItemIsValid_thenItemPosted() {
		// given
		ItemRequestDto itemRequestDto = itemRequestDto = ItemRequestDto.builder()
				.name("Item A")
				.description("Item A description")
				.available(true)
				.build();

		// when
		when(itemService.addItem(any(), anyInt())).then(returnsFirstArg());

		mvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1)
						.content(mapper.writeValueAsString(itemRequestDto)))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo("Item A"))
				);
	}

	@Test
	@SneakyThrows
	void patchItem_whenUserIsOwner_thenPatchedItemReturned() {
		// given
		Map<String, String> patch = new HashMap<>();
		patch.put("name", "updated Item A");
		ItemRequestDto patchedItemDto = ItemRequestDto.builder()
				.name("updated Item A")
				.description("")
				.available(true)
				.requestId(1)
				.build();
		// when
		when(itemService.patchItem(1, 1, patch)).thenReturn(patchedItemDto);

		mvc.perform(patch("/items/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1)
						.content(mapper.writeValueAsString(patch)))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo("updated Item A"))
				);
	}

	@Test
	@SneakyThrows
	void patchItem_whenUserIsNotOwner_thenUnauthorizedChangeExceptionThrown() {
		// given
		Map<String, String> patch = new HashMap<>();
		patch.put("name", "updated Item A");
		// when
		when(itemService.patchItem(anyInt(), anyInt(), any())).thenThrow(new UnauthorizedChangeException("Item", 1));

		mvc.perform(patch("/items/1")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1)
						.content(mapper.writeValueAsString(patch)))

				// then
				.andExpectAll(
						status().isNotFound()
				);
	}

	@Test
	@SneakyThrows
	void getItemById_whenItFound_thenItemReturned() {
		// given
		ItemWithBookingAndCommentsResponseDto responseDto = ItemWithBookingAndCommentsResponseDto.builder()
				.name("Item A")
				.description("Item A description")
				.available(true)
				.build();

		when(itemService.getItemById(anyInt(), anyInt())).thenReturn(responseDto);
		// when
		mvc.perform(get("/items/1")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1))

				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.name", equalTo("Item A"))
				);
		verify(itemService).getItemById(anyInt(), anyInt());
	}

	@Test
	@SneakyThrows
	void getItems() {
		// given
		ItemWithBookingResponseDto item1 = ItemWithBookingResponseDto.builder()
				.name("Item 1")
				.description("Item 1 description")
				.available(true)
				.build();
		ItemWithBookingResponseDto item2 = ItemWithBookingResponseDto.builder()
				.name("Item 2")
				.description("Item 2 description")
				.available(true)
				.build();

		List<ItemWithBookingResponseDto> users = List.of(item1, item2);
		// when
		when(itemService.getItems(1, null, null)).thenReturn(users);

		mvc.perform(get("/items")
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", 1))
				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(2)),
						jsonPath("$..name", hasItems("Item 1", "Item 2"))
				);
	}

	@Test
	@SneakyThrows
	void searchItems_whenTextNotBlank_thenListReturned() {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.builder()
				.name("Item A (something)")
				.description("Item A description")
				.available(true)
				.build();

		// when
		when(itemService.search("search text", null, null)).thenReturn(Collections.singletonList(itemRequestDto));
		mvc.perform(get("/items/search")
						.accept(MediaType.APPLICATION_JSON)
						.param("text", "search text"))
				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(1)),
						jsonPath("$..name", hasItems("Item A (something)"))
				);
	}

	@Test
	@SneakyThrows
	void searchItems_whenTextBlank_thenEmptyListReturned() {
		// when
		when(itemService.search("", null, null)).thenReturn(Collections.emptyList());
		mvc.perform(get("/items/search")
						.accept(MediaType.APPLICATION_JSON)
						.param("text", ""))
				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$", hasSize(0))
				);
	}

	@Test
	@SneakyThrows
	void postComment_whenUserHasFinishedBookings_thenCommentPosted() {
		// given
		CommentRequestDto commentRequestDto = CommentRequestDto.builder()
				.text("new comment")
				.build();
		CommentResponseDto commentResponseDto = CommentResponseDto.builder()
				.text("new comment")
				.authorName("Author")
				.created(LocalDateTime.now())
				.build();

		// when
		when(itemService.addComment(1, 1, commentRequestDto)).thenReturn(commentResponseDto);
		// then
		mvc.perform(post("/items/1/comment")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("itemId", "1")
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(commentRequestDto)))
				// then
				.andExpectAll(
						status().isOk(),
						jsonPath("$.text", equalTo("new comment"))
				);

	}

	@Test
	@SneakyThrows
	void postComment_whenUserNotHasFinishedBookings_thenStatusIsBadRequest() {
		// given
		CommentRequestDto commentRequestDto = CommentRequestDto.builder()
				.text("new comment")
				.build();
		CommentResponseDto commentResponseDto = CommentResponseDto.builder()
				.text("new comment")
				.authorName("Author")
				.created(LocalDateTime.now())
				.build();

		// when
		doThrow(UnauthorizedCommentException.class).when(itemService).addComment(anyInt(), anyInt(), any());
		// then
		mvc.perform(post("/items/1/comment")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.param("itemId", "1")
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(commentRequestDto)))
				// then
				.andExpectAll(
						status().isBadRequest()
				);

	}

}