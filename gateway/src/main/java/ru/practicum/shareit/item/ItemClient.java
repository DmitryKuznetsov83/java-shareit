package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.item.ItemRequestDto;


@Service
public class ItemClient extends BaseClient {

	private static final String API_PREFIX = "/items";

	@Autowired
	public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(HttpComponentsClientHttpRequestFactory::new)
						.build()
		);
	}

	public ResponseEntity<Object> postItem(ItemRequestDto itemRequestDto, Integer ownerId) {
		return post("", ownerId, itemRequestDto);
	}

	public ResponseEntity<Object> patchItem(Integer itemId, Integer userId, ItemRequestDto patch) {
		return patch("/" + itemId, userId, patch);
	}

	public ResponseEntity<Object> getItemById(Integer itemId, Integer userId) {
		return get("/" + itemId, userId);
	}

	public ResponseEntity<Object> getItems(Integer ownerId, Integer from, Integer size) {
		String path = "" + ((from != null) ? "&from=" + from + "&size=" + size : "");
		return get(path, ownerId.longValue());
	}

	public ResponseEntity<Object> search(String text, Integer from, Integer size) {
		String path = "/search?text=" + text + ((from != null) ? "&from=" + from + "&size=" + size : "");
		return get(path);
	}

	public ResponseEntity<Object> addComment(Integer itemId, Integer authorId, CommentRequestDto commentRequestDto) {
		return post("/" + itemId + "/comment", authorId, commentRequestDto);
	}

}
