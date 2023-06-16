package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.RequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

	private final RequestService requestService;
	private static final String USER_HEADER = "X-Sharer-User-Id";

	@PostMapping
	@Validated
	public RequestResponseDto postRequest(@Valid @RequestBody RequestRequestDto requestRequestDto, @RequestHeader(USER_HEADER) Integer requesterId) {
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
	public List<RequestResponseDto> getOthersRequests(@RequestParam(required = false) @PositiveOrZero Integer from,
	                                                  @RequestParam(required = false) @Positive Integer size,
	                                                  @RequestHeader(USER_HEADER) Integer userId) {
		return requestService.getOthersRequests(userId, from, size);
	}

}
