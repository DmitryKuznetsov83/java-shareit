package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestRequestDto;


@Service
public class RequestClient extends BaseClient {

	private static final String API_PREFIX = "/requests";

	@Autowired
	public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(HttpComponentsClientHttpRequestFactory::new)
						.build()
		);
	}


	public ResponseEntity<Object> postRequest(RequestRequestDto requestRequestDto, Integer requesterId) {
		return post("", requesterId, requestRequestDto);
	}

	public ResponseEntity<Object> getRequestById(Integer requestId, Integer userId) {
		return get("/" + requestId, userId);
	}

	public ResponseEntity<Object> getOwnRequests(Integer requesterId) {
		return get("", requesterId);
	}

	public ResponseEntity<Object> getOthersRequests(Integer userId, Integer from, Integer size) {
		String path = "/all" + ((from != null) ? "?from=" + from + "&size=" + size : "");
		return get(path, userId);
	}

}