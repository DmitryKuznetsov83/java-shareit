package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

	private final RequestService requestService;
	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	public RequestResponseDto postRequest(@RequestBody RequestRequestDto requestRequestDto, @RequestHeader(USER_HEADER) Integer requesterId) {
		return requestService.addRequest(requestRequestDto, requesterId);
	}

	@GetMapping("/{requestId}")
	public RequestResponseDto getRequest(@PathVariable Integer requestId, @RequestHeader(USER_HEADER) Integer userId) {
		return requestService.getRequestById(requestId, userId);
	}

	@GetMapping
	public List<RequestResponseDto> getOwnRequests(@RequestHeader(USER_HEADER) Integer requesterId) {
		return requestService.getOwnRequests(requesterId);
	}

	@GetMapping("/all")
	public List<RequestResponseDto> getOthersRequests(@RequestParam(required = false) Integer from,
	                                                  @RequestParam(required = false) Integer size,
	                                                  @RequestHeader(USER_HEADER) Integer userId) {
		return requestService.getOthersRequests(userId, from, size);
	}

}
