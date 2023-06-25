package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

	private final RequestClient requestClient;

	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	@Validated
	public ResponseEntity<Object> postRequest(@Valid @RequestBody RequestRequestDto requestRequestDto, @RequestHeader(USER_HEADER) Integer requesterId) {
		log.info("Create request {}, userId={}", requestRequestDto, requesterId);
		return requestClient.postRequest(requestRequestDto, requesterId);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequest(@PathVariable Integer requestId, @RequestHeader(USER_HEADER) Integer userId) {
		log.info("Get request by id {}, userId={}", requestId, userId);
		return requestClient.getRequestById(requestId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getOwnRequests(@RequestHeader(USER_HEADER) Integer requesterId) {
		log.info("Get own requests, ownerId={}", requesterId);
		return requestClient.getOwnRequests(requesterId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getOthersRequests(@RequestParam(required = false) @PositiveOrZero Integer from,
	                                                  @RequestParam(required = false) @Positive Integer size,
	                                                  @RequestHeader(USER_HEADER) Integer userId) {
		log.info("Get other's requests, userId={}, from={}, size={}", userId, from, size);
		return requestClient.getOthersRequests(userId, from, size);
	}

}
